/**
 * File: HiNfcParser.kt
 * Description: Utility object for parsing NFC tag data, including NDEF (NFC Data Exchange Format)
 *              messages and technology-specific information. Converts tag data into JSON format
 *              for easier handling and integration.
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

package com.mwkg.nfc.util

import android.nfc.NdefMessage
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.nfc.tech.Ndef
import android.nfc.tech.NfcA
import android.nfc.tech.NfcB
import android.nfc.tech.NfcBarcode
import android.nfc.tech.NfcF
import android.nfc.tech.NfcV
import android.util.Log
import com.mwkg.nfc.util.HiNfcToolkit.sanitizeToString
import org.json.JSONArray
import org.json.JSONObject

object HiNfcParser {
    // Converts NDEF messages and common NFC tag information into a JSON string
    fun readNdefDetailsAsJson(ndefMessages: List<NdefMessage>, tag: Tag): String {
        val tagInfo = mutableMapOf<String, Any>()
        // Add common tag information (UID, tech stack)
        addCommonTagInfo(tagInfo, tag)

        // Add NDEF-specific information
        val ndef = Ndef.get(tag)
        if (ndef != null) {
            // Process NDEF details
            processNdefInfo(tagInfo, ndef)
        }

        // Convert NDEF records into a JSON array and add them
        val ndefMessagesArray = JSONArray()
        ndefMessages.forEach {
            val ndefArray = readNdefMessageRecords(it)
            ndefMessagesArray.put(ndefArray)
        }
        tagInfo["NdefMessages"] = ndefMessagesArray

        return JSONObject(tagInfo as Map<*, *>).toString()
    }

    // Converts general NFC tag details into a JSON string (for tags without NDEF messages)
    fun readTagDetailsAsJson(tag: Tag): String {
        val tagInfo = mutableMapOf<String, Any>()
        // Add common tag information (UID, tech stack)
        addCommonTagInfo(tagInfo, tag)

        // Process NDEF-related information
        Ndef.get(tag)?.let {
            processNdefInfo(tagInfo, it)
        }

        // Process technology-specific information
        processTechInfo(tagInfo, tag)

        return JSONObject(tagInfo as Map<*, *>).toString()
    }

    // Adds common tag information such as UID and technology stack
    private fun addCommonTagInfo(tagInfo: MutableMap<String, Any>, tag: Tag) {
        tagInfo["uid"] = tag.id.joinToString("") { String.format("%02X", it) }
        tagInfo["techList"] = tag.techList.joinToString()
    }

    // Converts NDEF message records into a JSON array
    private fun readNdefMessageRecords(ndefMessage: NdefMessage): JSONArray {
        val recordsJsonArray = JSONArray()
        for (record in ndefMessage.records) {
            val recordJson = JSONObject().apply {
                put("tnf", record.tnf)                            // TNF (Type Name Format)
                put("type", record.type.sanitizeToString())       // Record type
                put("id", record.id?.sanitizeToString())          // Record ID
                put("payload", record.payload.sanitizeToString()) // Record payload
            }
            recordsJsonArray.put(recordJson)
        }
        return recordsJsonArray
    }

    // Processes NDEF-specific information, such as writability and cached messages
    private fun processNdefInfo(tagInfo: MutableMap<String, Any>, ndef: Ndef) {
        var isCanMakeReadOnly = false
        try {
            isCanMakeReadOnly = ndef.canMakeReadOnly()
        } catch (e: SecurityException) {
            Log.e("ModularX::HiNfcParser", "Error accessing NDEF data: ${e.message}")
        }

        tagInfo["Ndef"] = mapOf(
            "type" to ndef.type,
            "isWritable" to ndef.isWritable,
            "isConnected" to ndef.isConnected,
            "maxSize" to ndef.maxSize,
            "canMakeReadOnly" to isCanMakeReadOnly,
        )

        // Add cached NDEF records if available
        ndef.cachedNdefMessage?.let { ndefMessage ->
            val ndefArray = readNdefMessageRecords(ndefMessage)
            tagInfo["NdefRecords"] = ndefArray
        }
    }

    // Processes details for various NFC technologies (e.g., NfcA, IsoDep)
    private fun processTechInfo(tagInfo: MutableMap<String, Any>, tag: Tag) {
        // Processes NfcA (ISO 14443-3A) information
        val nfcA = NfcA.get(tag)
        if (nfcA != null) {
            tagInfo["NfcA"] = mapOf(
                "atqa" to nfcA.atqa.joinToString("") { String.format("%02X", it) },
                "sak" to String.format("%02X", nfcA.sak),
                "maxTransceiveLength" to nfcA.maxTransceiveLength,
                "timeout" to nfcA.timeout
            )
        }

        // Processes IsoDep (ISO 14443-4) information
        val isoDep = IsoDep.get(tag)
        if (isoDep != null) {
            tagInfo["IsoDep"] = mapOf(
                "historicalBytes" to (isoDep.historicalBytes?.joinToString("") { String.format("%02X", it) } ?: "N/A"),
                "hiLayerResponse" to (isoDep.hiLayerResponse?.joinToString("") { String.format("%02X", it) } ?: "N/A"),
                "maxTransceiveLength" to isoDep.maxTransceiveLength.toString(),
                "timeout" to isoDep.timeout
            )
        }

        // Processes NfcB (ISO 14443-3B) information
        val nfcB = NfcB.get(tag)
        if (nfcB != null) {
            tagInfo["NfcB"] = mapOf(
                "applicationData" to (nfcB.applicationData?.joinToString("") { String.format("%02X", it) } ?: "N/A"),
                "protocolInfo" to (nfcB.protocolInfo?.joinToString("") { String.format("%02X", it) } ?: "N/A")
            )
        }

        // Processes NfcF (Felica) information
        val nfcF = NfcF.get(tag)
        if (nfcF != null) {
            tagInfo["NfcF"] = mapOf(
                "manufacturer" to (nfcF.manufacturer?.joinToString("") { String.format("%02X", it) } ?: "N/A"),
                "systemCode" to (nfcF.systemCode?.joinToString("") { String.format("%02X", it) } ?: "N/A"),
                "maxTransceiveLength" to nfcF.maxTransceiveLength.toString(),
                "timeout" to nfcF.timeout.toString()
            )
        }

        // Processes NfcV (ISO 15693) information
        val nfcV = NfcV.get(tag)
        if (nfcV != null) {
            tagInfo["NfcV"] = mapOf(
                "responseFlags" to String.format("%02X", nfcV.responseFlags),
                "dsfId" to String.format("%02X", nfcV.dsfId),
                "maxTransceiveLength" to nfcV.maxTransceiveLength
            )
        }

        // Processes NfcBarcode information
        val nfcBarcode = NfcBarcode.get(tag)
        if (nfcBarcode != null) {
            tagInfo["NfcBarcode"] = mapOf(
                "type" to nfcBarcode.type
            )
        }

        // Processes MifareClassic information
        val mifareClassic = MifareClassic.get(tag)
        if (mifareClassic != null) {
            tagInfo["MifareClassic"] = mapOf(
                "type" to when (mifareClassic.type) {
                    MifareClassic.TYPE_CLASSIC -> "Classic"
                    MifareClassic.TYPE_PLUS -> "Plus"
                    MifareClassic.TYPE_PRO -> "Pro"
                    else -> "Unknown"
                },
                "size" to mifareClassic.size,
                "sectorCount" to mifareClassic.sectorCount,
                "blockCount" to mifareClassic.blockCount,
            )
        }

        // Processes MifareUltralight information
        val mifareUltralight = MifareUltralight.get(tag)
        if (mifareUltralight != null) {
            tagInfo["MifareUltralight"] = mapOf(
                "type" to when (mifareUltralight.type) {
                    MifareUltralight.TYPE_ULTRALIGHT -> "Ultralight"
                    MifareUltralight.TYPE_ULTRALIGHT_C -> "Ultralight C"
                    else -> "Unknown"
                }
            )
        }
    }
}
