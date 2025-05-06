package com.almalaundry.featured.order.data.dtos

import com.google.gson.annotations.SerializedName

data class UpdateStatusRequest(
    @SerializedName("status") val status: String
)