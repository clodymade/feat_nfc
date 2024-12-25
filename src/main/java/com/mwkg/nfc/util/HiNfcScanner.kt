/**
 * File: HiNfcScanner.kt
 * Description: NFC scanner utility for handling NFC operations, including scanning, reading NDEF messages,
 *              and processing various NFC tag technologies. Integrates with the activity lifecycle.
 *
 * Author: netcanis
 * Created: 2024-11-19
 *
 * License: MIT
 *
 * References:
 * - Android NFC API: https://developer.android.com/guide/topics/connectivity/nfc
 */

package com.mwkg.nfc.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.*
import android.nfc.tech.*
import android.os.Build
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.mwkg.nfc.model.HiNfcResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

@SuppressLint("StaticFieldLeak") // Suppresses memory leak warnings for static activity references
object HiNfcScanner : DefaultLifecycleObserver {
    private var callback: ((HiNfcResult) -> Unit)? = null
    private var nfcAdapter: NfcAdapter? = null
    private var isScanning = false
    private var activity: Activity? = null

    init {
        Log.d("ModularX::NfcScanner", "Initializing NFC Scanner")
    }

    // Initializes the NFC adapter
    private fun initialize() {
        if (nfcAdapter != null) return // Skip if already initialized
        try {
            val nfcManager = activity?.getSystemService(Context.NFC_SERVICE) as? NfcManager
            nfcAdapter = nfcManager?.defaultAdapter

            if (nfcAdapter == null) {
                Log.e("ModularX::NfcScanner", "NFC adapter is not available.")
            } else {
                Log.d("ModularX::NfcScanner", "NFC Scanner initialized successfully.")
            }
        } catch (e: Exception) {
            Log.e("ModularX::NfcScanner", "Error during NFC initialization: ${e.message}")
        }
    }

    // Starts NFC scanning
    fun start(
        activity: Activity,
        callback: (HiNfcResult) -> Unit
    ) {
        HiNfcScanner.activity = activity
        HiNfcScanner.callback = callback

        initialize()

        if (!hasRequiredPermissions()) {
            callback(HiNfcResult("", "Required permissions are missing."))
            return
        }

        if (isScanning) {
            Log.d("ModularX::HiNfcScanner", "Already scanning.")
            return
        }

        // Register with lifecycle owner
        (activity as? LifecycleOwner)?.lifecycle?.addObserver(this)

        Log.d("ModularX::NfcScanner", "NFC scanning started.")
    }

    // Stops NFC scanning
    fun stop() {
        if (isScanning) {
            disableForegroundDispatch()
            isScanning = false
            Log.d("ModularX::NfcScanner", "NFC scanning stopped.")
        }

        // Unregister from lifecycle owner
        (activity as? LifecycleOwner)?.lifecycle?.removeObserver(this)
    }

    // Checks if required NFC permissions are granted
    fun hasRequiredPermissions(): Boolean {
        if (nfcAdapter == null || nfcAdapter?.isEnabled == false) {
            Log.e("ModularX::NfcScanner", "NFC functionality is unavailable.")
            return false
        }
        return true
    }

    /**
     * Lifecycle event handling for DefaultLifecycleObserver
     */
    override fun onResume(owner: LifecycleOwner) {
        if (!isScanning && hasRequiredPermissions()) {
            enableForegroundDispatch()
            Log.d("ModularX::NfcScanner.onResume()", "Foreground Dispatch enabled.")
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        if (isScanning && hasRequiredPermissions()) {
            disableForegroundDispatch()
            Log.d("ModularX::NfcScanner.onPause()", "Foreground Dispatch disabled.")
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        stop() // Stop scanning
        super.onDestroy(owner)
    }

    // Enables foreground dispatch to prioritize NFC tag processing by this app
    private fun enableForegroundDispatch() {
        activity?.let {
            val pendingIntent = PendingIntent.getActivity(
                it,
                0,
                Intent(it, it::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            val filters = arrayOf(
                IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
                IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
            )

            nfcAdapter?.enableForegroundDispatch(it, pendingIntent, filters, null)
            isScanning = true
        } ?: Log.e("ModularX::NfcScanner", "Activity is required to enable Foreground Dispatch.")
    }

    // Disables foreground dispatch
    private fun disableForegroundDispatch() {
        activity?.let {
            nfcAdapter?.disableForegroundDispatch(it)
            isScanning = false
        } ?: Log.e("ModularX::NfcScanner", "Activity is required to disable Foreground Dispatch.")
    }

    // Handles NFC intents when a tag is detected
    fun handleNfcIntent(intent: Intent) {
        if (!hasRequiredPermissions()) {
            return
        }

        val tag: Tag? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        }

        tag?.let {
            CoroutineScope(Dispatchers.IO).launch {
                // Attempt to read NDEF messages
                val ndefRead = readNdefMessages(intent, tag)

                // If no NDEF messages, read general tag data
                if (!ndefRead) {
                    connectToTagAndReadData(it)
                }
            }
        } ?: run {
            callback?.let { it(HiNfcResult("", "No NFC tag detected.")) }
        }
    }

    // Reads NDEF messages from the detected tag
    private suspend fun readNdefMessages(intent: Intent, tag: Tag): Boolean = withContext(Dispatchers.Main) {
        val rawMessages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, NdefMessage::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        }

        val ndefMessages = rawMessages?.map { it as NdefMessage }
        if (!ndefMessages.isNullOrEmpty()) {
            val ndefInfoJsonString = HiNfcParser.readNdefDetailsAsJson(ndefMessages, tag)
            callback?.let { it(HiNfcResult(ndefInfoJsonString, "")) }
            return@withContext true
        }

        return@withContext false
    }

    // Connects to the NFC tag and reads data
    private fun connectToTagAndReadData(tag: Tag) {
        var ndef: Ndef? = null
        try {
            ndef = Ndef.get(tag)
            if (ndef != null) {
                ndef.connect()
                val ndefMessage = ndef.ndefMessage
                if (ndefMessage != null) {
                    val ndefInfoJsonString = HiNfcParser.readNdefDetailsAsJson(listOf(ndefMessage), tag)
                    callback?.let { it(HiNfcResult(ndefInfoJsonString, "")) }
                } else {
                    callback?.let { it(HiNfcResult("", "No NDEF messages available.")) }
                }
            } else {
                val tagInfoJsonString = HiNfcParser.readTagDetailsAsJson(tag)
                callback?.let { it(HiNfcResult(tagInfoJsonString, "")) }
            }
        } catch (e: IOException) {
            callback?.let { it(HiNfcResult("", "Failed to connect to NFC tag: ${e.message}")) }
        } finally {
            try {
                ndef?.close()
            } catch (e: IOException) {
                Log.e("ModularX::NfcScanner", "Error closing NDEF connection: ${e.message}")
            }
        }
    }
}
