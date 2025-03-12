plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")

}

android {
    namespace = "edu.aibu.ancat"
    compileSdk = 35

    defaultConfig {
        applicationId = "edu.aibu.ancat"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // Android core libraries
    implementation(libs.androidx.core.ktx) // Core KTX extensions for Android
    implementation(libs.androidx.lifecycle.runtime.ktx) // Lifecycle-aware components
    implementation(libs.androidx.activity.compose) // Compose support for Activities

    // Jetpack Compose libraries
    implementation(platform(libs.androidx.compose.bom)) // Compose BOM for version alignment
    implementation(libs.androidx.ui) // Basic UI components for Jetpack Compose
    implementation(libs.androidx.ui.graphics) // Compose graphics library
    implementation(libs.androidx.ui.tooling.preview) // Tooling for Compose UI preview
    implementation(libs.androidx.material3) // Material Design 3 components for Compose

    // Navigation
    implementation(libs.androidx.navigation.compose) // Navigation for Jetpack Compose

    // Serialization
    implementation(libs.kotlinx.serialization.core) // Core library for Kotlin Serialization

    // Testing libraries
    testImplementation(libs.junit) // JUnit for unit testing
    androidTestImplementation(libs.androidx.junit) // AndroidX JUnit extensions
    androidTestImplementation(libs.androidx.espresso.core) // Espresso for UI testing
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Compose BOM for testing
    androidTestImplementation(libs.androidx.ui.test.junit4) // Compose testing with JUnit 4
    debugImplementation(libs.androidx.ui.tooling) // Debug tools for Compose
    debugImplementation(libs.androidx.ui.test.manifest) // Manifest testing for Compose

    // Hilt for Dependency Injection
    implementation(libs.hilt.android) // Hilt DI library
    ksp(libs.hilt.android.compiler) // Hilt annotation processor
    implementation(libs.androidx.lifecycle.viewmodel.compose) // ViewModel support in Compose
    implementation(libs.androidx.hilt.navigation.compose) // Hilt navigation for Compose

    // Scanner dependencies
    implementation(libs.mlkit.document.scanner) // ML Kit Document Scanner
    implementation(libs.coil.compose) // Coil image loading for Compose

    // JSON serialization
    implementation(libs.kotlinx.serialization.json) // JSON serialization with Kotlin

    // Room Database
    implementation(libs.androidx.room.runtime) // Room database runtime
    ksp(libs.androidx.room.compiler) // Room annotation processor
    implementation(libs.androidx.room.ktx) // Room KTX extensions

    //QR Create
    implementation (libs.zxing.core)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

