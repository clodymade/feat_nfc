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
import com.mwkg.base.model.HiResult
import com.mwkg.nfc.model.HiNfcTag
import com.mwkg.nfc.util.HiNfcScanner
import com.mwkg.nfc.viewmodel.HiNfcTagListViewModel
import com.mwkg.util.hiToMapOrList
import com.mwkg.util.hiToPrettyJsonString

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

        // Start NFC scanning using HiNfcScanner
        HiNfcScanner.start(this) { result ->
            when (result) {
                is HiResult.HiNfcResult -> {
                    // Log the scanned NFC data as a pretty JSON string
                    Log.d("ModularX", result.nfcData.hiToPrettyJsonString())

                    // Convert the NFC data to a Map structure for further processing
                    val nfcMap = result.nfcData.hiToMapOrList() as? Map<String, Any> ?: emptyMap()
                    val uid = nfcMap["uid"] as? String ?: ""
                    val techList = nfcMap["techList"] as? String ?: ""
                    val ndef = nfcMap["Ndef"] as? Map<String, Any> ?: emptyMap()
                    val ndefRecords = nfcMap["NdefRecords"] as? List<Map<String, Any>> ?: emptyList()
                    val ndefMessages = nfcMap["NdefMessages"] as? List<List<Map<String, Any>>> ?: emptyList()

                    // Create an instance of HiNfcTag with the parsed data
                    val tag = HiNfcTag(
                        uid = uid,
                        techList = techList,
                        ndef = ndef,
                        ndefRecords = ndefRecords,
                        ndefMessages = ndefMessages,
                    )

                    // Update the ViewModel with the new tag
                    viewModel.update(tag)

                    // Optionally stop the scanner (commented out for continuous scanning)
                    // HiNfcScanner.stop()
                }
                else -> {
                    Log.e("ModularX::HiNfcTagListActivity", "Scan result error: ${result.error}")
                }
            }
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
