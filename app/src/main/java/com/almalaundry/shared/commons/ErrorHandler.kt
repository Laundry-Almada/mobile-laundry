package com.almalaundry.shared.commons

import org.json.JSONObject
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorHandler {
    fun getErrorMessage(e: Throwable): String {
        return when (e) {
            is HttpException -> {
                when (e.code()) {
                    400 -> "Permintaan tidak valid. Silakan periksa data yang dimasukkan."
                    401 -> "Sesi tidak valid. Silakan masuk kembali."
                    403 -> "Akses ditolak. Anda tidak memiliki izin untuk tindakan ini."
                    404 -> "Data tidak ditemukan."
                    429 -> "Terlalu banyak permintaan. Silakan coba lagi nanti."
                    500 -> "Terjadi masalah di server. Silakan coba lagi nanti."
                    else -> "Gagal memproses permintaan: ${e.message()}"
                }
            }

            is UnknownHostException -> "Koneksi internet tidak tersedia."
            is SocketTimeoutException -> "Koneksi terputus. Silakan coba lagi."
            is Exception -> {
                when {
                    e.message?.contains("User not logged in") == true -> "Silakan masuk untuk melanjutkan."
                    else -> "Terjadi kesalahan: ${e.message ?: "Tidak diketahui"}"
                }
            }

            else -> "Terjadi kesalahan yang tidak diketahui."
        }
    }

    fun parseApiError(errorBody: String?): String {
        if (errorBody.isNullOrBlank()) {
            return "Gagal memproses permintaan: Tidak ada detail kesalahan."
        }
        return try {
            val jsonObject = JSONObject(errorBody)
            val message = jsonObject.optString("message", "Gagal memproses permintaan")
            val errorDetail = jsonObject.optString("error", "")
            if (errorDetail.isNotEmpty()) "$message: $errorDetail" else message
        } catch (e: Exception) {
            "Gagal memproses permintaan: Format kesalahan tidak valid."
        }
    }
}