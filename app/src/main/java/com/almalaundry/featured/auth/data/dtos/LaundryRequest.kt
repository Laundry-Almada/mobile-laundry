package com.almalaundry.featured.auth.data.dtos

import com.google.gson.annotations.SerializedName

data class LaundryRequest(
    @SerializedName("name") val name: String,
    @SerializedName("address") val address: String,
    @SerializedName("phone") val phone: String

)