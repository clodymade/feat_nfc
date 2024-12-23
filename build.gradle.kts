plugins {
    alias(libs.plugins.android.library) // 안드로이드 라이브러리 모듈을 위한 Gradle 플러그인을 참조
    alias(libs.plugins.jetbrains.kotlin.android) // **코틀린(Android 환경용)**을 위한 Gradle 플러그인을 참조
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
            isMinifyEnabled = true // R8 또는 ProGuard를 사용하여 코드 난독화 및 최적화
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
        buildConfig = true  // Gradle은 각 빌드 타입마다 BuildConfig 클래스를 생성(DEBUG, APPLICATION_ID, VERSION_CODE 등). (기본값: true)
        viewBinding = true  // XML 레이아웃 파일과 연결된 타입 안전한 뷰 바인딩 클래스가 자동으로 생성. (기본값: false)
        compose = true      // Jetpack Compose를 활성화하기
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

dependencies {
//    implementation(project(":feat_global"))
//    implementation(project(":feat_util"))
//    implementation(project(":feat_base"))
    api(project(":feat_global"))
    api(project(":feat_util"))
    api(project(":feat_base"))

    // AndroidX Core 및 Compose
    implementation(libs.androidx.core.ktx) // Core KTX
    implementation(libs.androidx.appcompat) // AppCompat
    implementation(libs.material) // Google Material

    // Compose BOM (버전 관리)
    implementation(platform(libs.androidx.compose.bom.v20240100))

    // Compose UI 및 Material
    implementation(libs.androidx.ui) // Compose UI
    implementation(libs.androidx.material3) // Material 3
    implementation(libs.androidx.lifecycle.viewmodel.compose) // ViewModel Compose
    implementation(libs.androidx.activity.compose) // Activity Compose

    // 추가 종속성
    debugImplementation(libs.androidx.compose.ui.ui.tooling)

    // Core KTX 최신 버전
    implementation(libs.androidx.core.ktx) // Core KTX v1.15.0 (BLE 포함)

    // 테스트 의존성
    testImplementation(libs.junit) // JUnit
    androidTestImplementation(libs.androidx.junit) // AndroidX JUnit
    androidTestImplementation(libs.androidx.espresso.core) // Espresso UI 테스트
}