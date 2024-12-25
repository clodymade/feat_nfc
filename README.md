# **feat_nfc**

A **feature module** for NFC tag scanning and NDEF parsing on Android.

---

## **Overview**

`feat_nfc` is an Android module designed to:
- Scan and manage NFC tags.
- Parse and display NDEF (NFC Data Exchange Format) messages.
- Provide a modern, responsive UI using **Jetpack Compose**.

This module is compatible with **Android 11 (API 30)** and above, with support for **Android Jetpack libraries** and **Kotlin Coroutines**.

---

## **Features**

- ✅ **NFC Tag Scanning**: Detect NFC tags and retrieve detailed information, including UID, tech stack, and NDEF data.
- ✅ **NDEF Parsing**: Extract payloads, type, and TNF (Type Name Format) from NDEF messages.
- ✅ **Responsive UI**: Display scanned NFC tags in a modern UI built with Jetpack Compose.
- ✅ **Lifecycle-Aware**: Integrates with Android lifecycle components for seamless operation.
- ✅ **Modular Architecture**: Lightweight and easy to integrate into existing Android projects.

---

## **Requirements**

| Requirement        | Minimum Version         |
|--------------------|-------------------------|
| **Android OS**     | 11 (API 30)             |
| **Kotlin**         | 1.9.22                  |
| **Android Studio** | Giraffe (2022.3.1)      |
| **Gradle**         | 8.0                     |

---

## **Setup**

### **1. Add feat_nfc to Your Project**

Include `feat_nfc` as a module in your project. Add the following to your `settings.gradle` file:

```gradle
include ':feat_nfc'
```

Then, add it as a dependency in your app module’s build.gradle file:
```gradle
implementation project(":feat_nfc")
```

### **2. Permissions**

Add the required permissions to your AndroidManifest.xml:

```xml
<!-- NFC Permissions -->
<uses-permission android:name="android.permission.NFC" />

<!-- Optional permission for accessing preferred payment information (Android 12 and above) -->
<uses-permission android:name="android.permission.NFC_PREFERRED_PAYMENT_INFO" />

<!-- Declare NFC feature -->
<uses-feature android:name="android.hardware.nfc" android:required="false" />
```

---

## **Usage**

### **1. Start NFC Scanning**

Start NFC scanning using HiNfcScanner:

```kotlin
HiNfcScanner.start(this) { result ->
    val nfcMap = result.nfcData.hiToMapOrList() as? Map<String, Any> ?: emptyMap()
    println("NFC Tag Found:")
    println("UID: ${nfcMap["uid"]}")
    println("Tech List: ${nfcMap["techList"]}")
    println("NDEF: ${nfcMap["Ndef"]}")
    println("NdefRecords: ${nfcMap["NdefRecords"]}")
    println("NdefMessages: ${nfcMap["NdefMessages"]}")
}
```

### **2. Stop NFC Scanning**

Stop scanning when it’s no longer needed:

```kotlin
HiNfcScanner.stop()
```

### **3. Handle NFC Intents**

To process detected NFC tags when the app is in the foreground, handle the onNewIntent event in your activity:

```kotlin
override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    HiNfcScanner.handleNfcIntent(intent)
}
```

---

## **HiNfcResult**

The NFC scan results are encapsulated in the HiNfcResult class. Key properties include:

| Property          | Type             | Description                         |
|-------------------|------------------|-------------------------------------|
| nfcData           | Map<String, Any> | Parsed data from the NFC tag.       |
| error             | String           | Error message, if any.              |

---

## **Example UI**

To display NFC devices in a Jetpack Compose UI:

```kotlin
@Composable
fun HiNfcTagListScreen(
    nfcTags: StateFlow<List<HiNfcTag>>,
    onBackPressed: () -> Unit
) {
    val nfcTagList by nfcTags.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NFC Tag List") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.Close, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(nfcTagList) { tag ->
                HiNfcTagItem(tag)
            }
        }
    }
}
```

---

## **License**

feat_nfc is available under the MIT License. See the LICENSE file for details.

---

## **Contributing**

Contributions are welcome! To contribute:

1. Fork this repository.
2. Create a feature branch:
```
git checkout -b feature/your-feature
```
3. Commit your changes:
```
git commit -m "Add feature: description"
```
4. Push to the branch:
```
git push origin feature/your-feature
```
5. Submit a Pull Request.

---

## **Author**

### **netcanis**
iOS GitHub: https://github.com/netcanis
Android GitHub: https://github.com/clodymade

---

