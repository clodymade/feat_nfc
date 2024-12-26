plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    `maven-publish` // Maven publishing for JitPack
}

android {
    namespace = "com.mwkg.nfc"
    compileSdk = 35

    defaultConfig {
        minSdk = 31

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
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

// Maven publishing configuration for JitPack
publishing {
    publications {
        create<MavenPublication>("release") {
            afterEvaluate {
                from(components["release"])
            }

            // Define Maven artifact metadata
            groupId = "com.github.clodymade"    // GitHub username as group ID
            artifactId = "feat_nfc"             // Module name as artifact ID
            version = "1.0.1"                   // Version matching the Git tag

            // Configure POM metadata
            pom {
                name.set("feat_nfc")
                description.set("A utility library for Android apps.")
                url.set("https://github.com/clodymade/feat_nfc")

                licenses {
                    license {
                        name.set("MIT License") // License type
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("clodymade")
                        name.set("netcanis")
                        email.set("netcanis@gmail.com")
                    }
                }

                scm {
                    // Source Control Management (SCM) details
                    connection.set("scm:git:git://github.com/clodymade/feat_nfc.git")
                    developerConnection.set("scm:git:ssh://git@github.com:clodymade/feat_nfc.git")
                    url.set("https://github.com/clodymade/feat_nfc")
                }
            }
        }
    }
}
