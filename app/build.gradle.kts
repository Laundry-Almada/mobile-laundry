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
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }
    
    signingConfigs {
        create("release") {
            val keyProperties = Properties().apply {
                load(rootProject.file(".env.production").inputStream())
            }
            storeFile =
                file(keyProperties.getProperty("RELEASE_STORE_FILE", "release-keystore.jks"))
            storePassword = keyProperties.getProperty("RELEASE_STORE_PASSWORD")
            keyAlias = keyProperties.getProperty("RELEASE_KEY_ALIAS")
            keyPassword = keyProperties.getProperty("RELEASE_KEY_PASSWORD")
        }
    }

    buildTypes {
        debug {
            val properties = Properties().apply {
                load(rootProject.file(".env.development").inputStream())
            }
            buildConfigField("String", "BASE_URL", "\"${properties.getProperty("BASE_URL")}\"")
        }
        release {
            val properties = Properties().apply {
                load(rootProject.file(".env.production").inputStream())
            }
            buildConfigField("String", "BASE_URL", "\"${properties.getProperty("BASE_URL")}\"")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
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
    implementation(libs.play.services.cast.framework)
    //  implementation(libs.firebase.crashlytics.buildtools)
    //  Hilt dagger
    ksp(libs.dagger.compiler) // Dagger compiler
    ksp(libs.hilt.android.compiler)   // Hilt compiler
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // compose
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.animation)
    // jetpack data store
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.core)

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

    implementation(libs.core)

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

    // testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk) // Untuk mocking
    testImplementation(libs.mockk.android)
    testImplementation(libs.kotlinx.coroutines.test) // Untuk Coroutines Test
    testImplementation(libs.androidx.core.testing) // Untuk InstantTaskExecutorRule
    testImplementation(libs.hilt.android.testing) // Untuk Hilt testing
    testAnnotationProcessor(libs.hilt.compiler)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //image slider
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)
//    implementation(libs.androidx.foundation)

    //chart
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m2)
    implementation(libs.vico.compose.m3)
    implementation(libs.vico.views)

    // utils
    implementation(libs.kotlinx.datetime)
}
