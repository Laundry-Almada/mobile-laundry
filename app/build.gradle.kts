import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    kotlin("plugin.serialization") version "2.0.21"
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.almalaundry"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.almalaundry"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            val properties = Properties()
            properties.load(rootProject.file(".env.development").inputStream())

            buildConfigField(
                "String", "BASE_URL", "\"${properties.getProperty("BASE_URL")}\""
            )
        }
        release {
            val properties = Properties()
            properties.load(rootProject.file(".env.production").inputStream())

            buildConfigField(
                "String", "BASE_URL", "\"${properties.getProperty("BASE_URL")}\""
            )
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        allWarningsAsErrors = false
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
        )
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.espresso.core)
    //  implementation(libs.firebase.crashlytics.buildtools)
    //  Hilt dagger
    //  ksp(libs.dagger.compiler) // Dagger compiler
    ksp(libs.hilt.android.compiler)   // Hilt compiler
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)

    // compose
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.animation)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // Kotlin serialization
    implementation(libs.kotlinx.serialization.json)

    // icon
    implementation(libs.icons.lucide.android)
    implementation(libs.font.awesome)

    // barcode generate
    implementation(libs.core)

    // ML Kit Barcode Scanning
    implementation(libs.barcode.scanning)

    // CameraX
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // Coil
    implementation(libs.coil.compose)

    // Shimmer shimmer
    implementation(libs.shimmer)
    implementation(libs.compose.shimmer)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
