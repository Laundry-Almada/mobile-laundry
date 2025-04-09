## ✅ Feature Progress

### Authentication

| Status | Fungsional |
|:------:|------------|
|   ✅    | login      |
|   🚧   | register   |
|   ✅    | logout     |

### Barcode Scanner

| Status | Fungsional                                        |
|:------:|---------------------------------------------------|
|   ✅    | Scan Barcode                                      |
|   ✅    | Get Barcode Information                           |
|   ✅    | print Barcode & connect to physcical print device |

### Dashboard

| Status | Fungsional |
|:------:|------------|
|   🚧    | Statistik  |
|   🚧    | Notif      |

### Profile

| Status | Fungsional     |
|:------:|----------------|
|   🚧   | Get Profile    |
|   🚧   | Update Profile |

### Order

| Status | Fungsional                       |
|:------:|----------------------------------|
|   ✅    | List Order                       |
|   ✅    | Create Order                     |
|   ✅    | Edit Order & Update Order Status |
|   ✅    | Filtering order                  |
|   ✅    | Searching order                  |
|   ✅    | Delete Order                     |
|   ✅    | Send Nota/message whatsapp       |

### History Order

| Status | Fungsional                    |
|:------:|-------------------------------|
|   ✅    | List Order yang sudah selesai |
|   ✅    | Filtering order               |

### Guest

| Status | Fungsional                                |
|:------:|-------------------------------------------|
|   🚧    | Cek status Order by customer guest/public |

---

### Legend:

- ✅ = Completed
- 🚧 = In Progress
- ⬜ = Not started


# Dokumentasi Aplikasi Android: AlmaLaundry

## Deskripsi Umum
**AlmaLaundry** adalah aplikasi Android yang dikembangkan menggunakan Android Studio Ladybug dengan Jetpack Compose sebagai UI framework utama. Aplikasi ini dirancang untuk mendukung fitur modern seperti navigasi, pengelolaan data lokal, integrasi API, pemindaian barcode, dan visualisasi data dengan chart. Proyek ini menggunakan bahasa pemrograman Kotlin dan memanfaatkan berbagai library populer untuk mempercepat pengembangan.

Tanggal pembuatan dokumentasi: **9 April 2025**.

## Spesifikasi Teknis
- **IDE**: Android Studio Ladybug
- **Bahasa Pemrograman**: Kotlin 2.1.10
- **Target SDK**: 35
- **Minimum SDK**: 24
- **Compile SDK**: 35
- **Java Version**: 11
- **Version Code**: 1
- **Version Name**: 1.0
- **Namespace/Application ID**: `com.almalaundry`

## Struktur Build
Proyek ini menggunakan Gradle dengan file konfigurasi `build.gradle` dan manajemen dependensi melalui file `libs.toml` untuk mempermudah pembaruan versi library.

### Plugins
- **Android Application**: `com.android.application` (v8.8.2)
- **Kotlin Android**: `org.jetbrains.kotlin.android` (v2.1.10)
- **Kotlin Compose**: `org.jetbrains.kotlin.plugin.compose` (v2.1.10)
- **Kotlin Serialization**: `kotlin("plugin.serialization")` (v2.0.21)
- **Google KSP**: `com.google.devtools.ksp`
- **Hilt Android**: `com.google.dagger.hilt.android`

### Konfigurasi Android
- **Build Types**:
  - **Debug**: Menggunakan file `.env.development` untuk BASE_URL.
  - **Release**: Menggunakan file `.env.production` untuk BASE_URL, dengan minifikasi kode (`isMinifyEnabled = true`) dan aturan ProGuard.
- **Build Features**:
  - Jetpack Compose diaktifkan (`compose = true`).
  - BuildConfig diaktifkan (`buildConfig = true`).
- **Compile Options**: Menggunakan Java 11 untuk source dan target compatibility.
- **Kotlin Options**: JVM target 11, dengan opt-in untuk API eksperimental Material3.

## Dependensi
Berikut adalah daftar dependensi utama yang digunakan dalam proyek ini, dikelompokkan berdasarkan fungsinya:

### Jetpack Compose
- `androidx.compose.bom:2025.02.00` - BOM untuk konsistensi versi Compose.
- `androidx.compose.ui:ui` - UI toolkit Compose.
- `androidx.compose.material3:material3` - Komponen Material Design 3.
- `androidx.activity:activity-compose:1.10.1` - Integrasi Compose dengan Activity.
- `androidx.navigation:navigation-compose:2.8.8` - Navigasi berbasis Compose.

### Dependency Injection (Hilt)
- `com.google.dagger:hilt-android:2.55` - Hilt untuk DI.
- `androidx.hilt:hilt-navigation-compose:1.2.0` - Hilt dengan navigasi Compose.
- `com.google.dagger:dagger-compiler:2.55` - Kompilator Dagger.

### Networking
- `com.squareup.retrofit2:retrofit:2.11.0` - Library HTTP client.
- `com.squareup.retrofit2:converter-gson:2.11.0` - Konversi JSON dengan Gson.
- `com.squareup.okhttp3:logging-interceptor:4.12.0` - Logging untuk debugging HTTP.

### Data Lokal
- `androidx.datastore:datastore-preferences:1.1.3` - Penyimpanan data lokal berbasis Jetpack DataStore.
- `org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0` - Serialisasi JSON.

### Kamera & Barcode
- `androidx.camera:camera-camera2:1.4.1` - CameraX untuk akses kamera.
- `com.google.mlkit:barcode-scanning:17.3.0` - Pemindaian barcode dengan ML Kit.
- `com.google.zxing:core:3.5.3` - Generasi barcode.

### UI dan Visualisasi
- `io.coil-kt.coil3:coil-compose:3.1.0` - Pemuatan gambar dengan Coil.
- `com.facebook.shimmer:shimmer:0.5.0` - Efek shimmer untuk loading.
- `com.valentinilk.shimmer:compose-shimmer:1.3.2` - Shimmer untuk Compose.
- `com.google.accompanist:accompanist-pager:0.32.0` - Image slider.
- `com.patrykandpatrick.vico:compose:2.0.2` - Library chart untuk Compose.

### Ikon
- `br.com.devsrsouza.compose.icons:font-awesome:1.1.1` - Ikon Font Awesome.
- `com.composables:icons-lucide-android:1.0.0` - Ikon Lucide.

### Testing
- `junit:junit:4.13.2` - Unit testing.
- `androidx.test.ext:junit:1.2.1` - JUnit untuk Android.
- `androidx.test.espresso:espresso-core:3.6.1` - UI testing.
- `androidx.compose.ui:ui-test-junit4` - Testing untuk Compose.

### Core Libraries
- `androidx.core:core-ktx:1.15.0` - Ekstensi Kotlin untuk Android.
- `androidx.lifecycle:lifecycle-runtime-ktx:2.8.7` - Lifecycle-aware components.

## Cara Menjalankan Aplikasi
1. **Prasyarat**:
   - Android Studio Ladybug terinstal.
   - JDK 11 terkonfigurasi.
   - Emulator atau perangkat fisik dengan API 24 atau lebih tinggi.
2. **Langkah-langkah**:
   - Clone repository proyek.
   - Buka proyek di Android Studio.
   - Tambahkan file `.env.development` dan `.env.production` di root proyek dengan variabel `BASE_URL`.
   - Sinkronkan proyek dengan Gradle (`Sync Project with Gradle Files`).
   - Pilih build variant (Debug/Release) dan jalankan aplikasi melalui emulator atau perangkat.

## Catatan Tambahan
- Aplikasi ini menggunakan Material Design 3 untuk antarmuka pengguna.
- Fitur eksperimental seperti `ExperimentalMaterial3Api` diaktifkan melalui opt-in di `kotlinOptions`.
- Pastikan file `.env` tidak di-commit ke version control untuk keamanan.

---
