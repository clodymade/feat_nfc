/**
 * File: HiResult.kt
 *
 * Description: This sealed class defines various scan result types, including QR codes, NFC tags, BLE devices, Beacons, and OCR data.
 *              Each result class contains relevant details such as scanned data, error messages, and specific attributes.
 *
 * Author: netcanis
 * Created: 2024-11-19
 *
 * License: MIT
 *
 * References:
 * - Android BLE Overview: https://developer.android.com/guide/topics/connectivity/bluetooth/ble-overview
 */

package com.mwkg.nfc.model

/**
 * Represents the result of an NFC scan.
 *
 * @property nfcData The data string scanned from the NFC tag.
 * @property error The error message that occurred during scanning.
 */
data class HiNfcResult(
    val nfcData: String,
    val error: String = ""
)
