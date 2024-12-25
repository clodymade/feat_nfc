/**
 * File: HiNfcTagListViewModel.kt
 * Description: ViewModel for managing the list of scanned NFC tags.
 *              Uses StateFlow for reactive UI updates in Jetpack Compose.
 *
 * Author: netcanis
 * Created: 2024-11-19
 *
 * License: MIT
 *
 * References:
 * - StateFlow Documentation: https://developer.android.com/kotlin/flow/stateflow-and-sharedflow
 * - ViewModel Documentation: https://developer.android.com/topic/libraries/architecture/viewmodel
 */

package com.mwkg.nfc.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.mwkg.nfc.model.HiNfcTag

/**
 * ViewModel for managing and updating a list of HiNfcTag objects.
 */
class HiNfcTagListViewModel : ViewModel() {

    // Private mutable state for holding the list of NFC tags
    private val _tags = MutableStateFlow<List<HiNfcTag>>(emptyList())

    // Public immutable state for observing NFC tags from the UI
    val tags: StateFlow<List<HiNfcTag>> get() = _tags

    /**
     * Updates the NFC tag list. If the tag already exists (based on UID, tech list, and payload),
     * it replaces the existing entry. Otherwise, it adds the new tag to the list.
     *
     * @param tag The HiNfcTag object to be added or updated in the list.
     */
    fun update(tag: HiNfcTag) {
        val currentTags = _tags.value.toMutableList()

        // Find the index of the existing tag (if any) based on unique criteria
        val existingIndex = currentTags.indexOfFirst {
            (it.uid == tag.uid && it.uid.isNotEmpty()) && // Match based on UID
                    (it.techList == tag.techList) &&      // Match based on technology list
                    (it.ndefRecords.first()["payload"] == tag.ndefRecords.first()["palyload"]) // Match based on payload
        }

        // Replace the existing tag or add a new one
        if (existingIndex >= 0) {
            currentTags[existingIndex] = tag
        } else {
            currentTags.add(tag)
        }

        // Update the StateFlow with the new immutable list
        _tags.value = currentTags.toList()
    }
}
