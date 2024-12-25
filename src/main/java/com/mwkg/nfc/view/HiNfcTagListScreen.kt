/**
 * File: HiNfcTagListScreen.kt
 * Description: Composable function for displaying a list of scanned NFC tags using Jetpack Compose.
 *              Integrates with a StateFlow to reactively update the UI when new tags are added.
 *
 * Author: netcanis
 * Created: 2024-11-19
 *
 * License: MIT
 *
 * References:
 * - Jetpack Compose LazyColumn: https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/LazyColumn
 * - Jetpack Compose Scaffold: https://developer.android.com/reference/kotlin/androidx/compose/material3/Scaffold
 */

package com.mwkg.nfc.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mwkg.nfc.model.HiNfcTag
import kotlinx.coroutines.flow.StateFlow

/**
 * Displays a scrollable list of NFC tags in a modern UI using Jetpack Compose.
 *
 * @param nfcTags StateFlow containing a list of HiNfcTag objects representing the scanned NFC tags.
 * @param onBackPressed Callback invoked when the user presses the back button in the top app bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiNfcTagListScreen(
    nfcTags: StateFlow<List<HiNfcTag>>, // Reactive flow of NFC tags
    onBackPressed: () -> Unit          // Action to perform on back press
) {
    // Collect the current list of NFC tags from the StateFlow
    val nfcTagList by nfcTags.collectAsState()

    Scaffold(
        // Top App Bar with a title and back button
        topBar = {
            TopAppBar(
                title = { Text("NFC Tag List") }, // Title of the screen
                navigationIcon = {
                    IconButton(onClick = onBackPressed) { // Back button
                        Icon(Icons.Default.Close, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        // LazyColumn for rendering the list of NFC tags
        LazyColumn(
            modifier = Modifier.padding(paddingValues), // Respect scaffold padding
            contentPadding = PaddingValues(16.dp)      // Padding around the list content
        ) {
            // Render each NFC tag using HiNfcTagItem composable
            items(nfcTagList) { tag ->
                HiNfcTagItem(tag)
            }
        }
    }
}
