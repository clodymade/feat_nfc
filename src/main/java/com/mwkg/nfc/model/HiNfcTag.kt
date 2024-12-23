/**
 * File: HiNfcTag.kt
 * Description: Data class representing an NFC tag with its unique ID (UID), supported technologies,
 *              and parsed NDEF (NFC Data Exchange Format) information including records and messages.
 *
 * Author: netcanis
 * Created: 2024-11-19
 *
 * License: MIT
 *
 * References:
 * - Android NFC API: https://developer.android.com/guide/topics/connectivity/nfc
 * - NDEF Specification: https://nearfieldcommunication.org/ndef.html
 */

package com.mwkg.nfc.model

/**
 * Represents the data structure of an NFC tag.
 *
 * @property uid The unique identifier (UID) of the NFC tag.
 * @property techList A comma-separated string listing the NFC technologies supported by the tag.
 * @property ndef A map containing general NDEF information (e.g., type, size, etc.).
 * @property ndefRecords A list of parsed NDEF records, where each record is represented as a map of key-value pairs.
 * @property ndefMessages A list of NDEF messages, where each message contains a list of records represented as maps.
 */
data class HiNfcTag(
    val uid: String,
    val techList: String,
    val ndef: Map<String, Any>,
    val ndefRecords: List<Map<String, Any>>,
    val ndefMessages: List<List<Map<String, Any>>>
)
