/**
 * File: HiNfcTagItem.kt
 * Description: Composable function to display NFC tag information in a card layout using Jetpack Compose.
 *              Displays details such as UID, supported technologies, and NDEF data.
 *
 * Author: netcanis
 * Created: 2024-11-19
 *
 * License: MIT
 *
 * References:
 * - Jetpack Compose Documentation: https://developer.android.com/jetpack/compose
 */

package com.mwkg.nfc.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mwkg.nfc.model.HiNfcTag
import com.mwkg.nfc.util.HiToolkit.toPrettyJsonString

/**
 * Composable function to display an NFC tag's details in a card format.
 *
 * @param tag An instance of HiNfcTag containing the NFC tag's data.
 */
@Composable
fun HiNfcTagItem(tag: HiNfcTag) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), // Adds vertical padding between cards
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Card elevation for shadow effect
    ) {
        Column(modifier = Modifier.padding(16.dp)) { // Adds padding inside the card
            // Display each NFC tag detail as a text component
            HiNfcTagDetailText(label = "UID", value = tag.uid)
            HiNfcTagDetailText(label = "Tech List", value = tag.techList)
            HiNfcTagDetailText(label = "NDEF", value = tag.ndef.toPrettyJsonString())
            HiNfcTagDetailText(label = "NDEF Records", value = tag.ndefRecords.toPrettyJsonString())
            HiNfcTagDetailText(label = "NDEF Messages", value = tag.ndefMessages.toPrettyJsonString())
        }
    }
}

/**
 * Composable function to display a single NFC tag detail.
 *
 * @param label The label for the detail (e.g., "UID", "Tech List").
 * @param value The value associated with the label.
 */
@Composable
fun HiNfcTagDetailText(label: String, value: String) {
    Text(
        text = "$label: $value", // Displays the detail in "Label: Value" format
        style = MaterialTheme.typography.bodyMedium // Uses MaterialTheme for consistent styling
    )
}
