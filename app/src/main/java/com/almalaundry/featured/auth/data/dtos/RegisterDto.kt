//package com.almalaundry.featured.auth.data.dtos
//
//import kotlinx.serialization.Serializable
//
//@Serializable
//data class RegisterDto(
//    val username: String,
//    val password: String,
//    val email: String,
//    val confirmPassword: String,
//    val role: String,
//    val laundryName: String? = null,
//    val selectedLaundry: String? = null
//)

package com.almalaundry.featured.auth.data.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RegisterDto(
    val username: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val role: String,
    val laundryName: String? = null,
    val selectedLaundry: String? = null
)