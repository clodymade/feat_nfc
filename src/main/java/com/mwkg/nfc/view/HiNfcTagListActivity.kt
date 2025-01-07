/**
 * File: HiNfcTagListActivity.kt
 * Description: Activity for managing and displaying a list of NFC tags using Jetpack Compose.
 *              Integrates NFC scanning via HiNfcScanner and updates the UI with detected tags.
 *
 * Author: netcanis
 * Created: 2024-11-19
 *
 * License: MIT
 *
 * References:
 * - Android NFC API: https://developer.android.com/guide/topics/connectivity/nfc
 * - Jetpack Compose Activity Integration: https://developer.android.com/jetpack/compose
 */

package com.mwkg.nfc.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.mwkg.nfc.model.HiNfcTag
import com.mwkg.nfc.util.HiNfcScanner
import com.mwkg.nfc.util.HiNfcToolkit.toMapOrList
import com.mwkg.nfc.viewmodel.HiNfcTagListViewModel

/**
 * Activity for scanning NFC tags and displaying the results in a list.
 * Uses HiNfcScanner for NFC operations and a ViewModel for state management.
 */
class HiNfcTagListActivity : ComponentActivity() {

    // ViewModel to manage the list of detected NFC tags
    private val viewModel: HiNfcTagListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up the UI using Jetpack Compose
        setContent {
            HiNfcTagListScreen(
                nfcTags = viewModel.tags,       // Pass the NFC tags to the Composable
                onBackPressed = { finish() }    // Handle back press
            )
        }

        startNfcScanner()
    }

    private fun startNfcScanner() {
        // Start NFC scanning using HiNfcScanner
        HiNfcScanner.start(this) { result ->
            // Convert the scanned NFC data into a Map structure
            val nfcMap = result.nfcData.toMapOrList() as? Map<String, Any> ?: emptyMap()

            // Extract values from the NFC data map
            val tag = HiNfcTag(
                uid = nfcMap["uid"] as? String ?: "",
                techList = nfcMap["techList"] as? String ?: "",
                ndef = nfcMap["Ndef"] as? Map<String, Any> ?: emptyMap(),
                ndefRecords = nfcMap["NdefRecords"] as? List<Map<String, Any>> ?: emptyList(),
                ndefMessages = nfcMap["NdefMessages"] as? List<List<Map<String, Any>>> ?: emptyList()
            )

            // Update the ViewModel with the parsed tag data
            viewModel.update(tag)

            // Log the tag data for debugging purposes
            Log.d("ModularX", tag.toString())

            // Optionally stop the scanner (commented out for continuous scanning)
            // HiNfcScanner.stop()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        // Handle the NFC tag when detected
        HiNfcScanner.handleNfcIntent(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop NFC scanning when the activity is destroyed
        HiNfcScanner.stop()
    }
}
