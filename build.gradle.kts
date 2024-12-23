plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.mwkg.nfc"
    compileSdk = 35
    ndkVersion = "25.1.8937393"
    buildToolsVersion = "35.0.0"

    defaultConfig {
        minSdk = 31

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        targetSdk = 35
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    lint {
        targetSdk = 35
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

dependencies {
    api(project(":feat_global"))
    api(project(":feat_util"))
    api(project(":feat_base"))

    // AndroidX Core 및 Compose
    implementation(libs.androidx.core.ktx) // Core KTX
    implementation(libs.androidx.appcompat) // AppCompat
    implementation(libs.material) // Google Material

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom.v20240100))

    // Compose UI, Material
    implementation(libs.androidx.ui) // Compose UI
    implementation(libs.androidx.material3) // Material 3
    implementation(libs.androidx.lifecycle.viewmodel.compose) // ViewModel Compose
    implementation(libs.androidx.activity.compose) // Activity Compose

    debugImplementation(libs.androidx.compose.ui.ui.tooling)

    // Core KTX
    implementation(libs.androidx.core.ktx) // Core KTX v1.15.0

    testImplementation(libs.junit) // JUnit
    androidTestImplementation(libs.androidx.junit) // AndroidX JUnit
    androidTestImplementation(libs.androidx.espresso.core) // Espresso UI Test
}
