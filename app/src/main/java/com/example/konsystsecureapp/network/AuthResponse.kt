package com.example.konsystsecureapp.network

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("protectedToken") val protectedToken: String,
    @SerializedName("username") val username: String,
    @SerializedName("userId") val userId: Int,
    @SerializedName("userNickname") val userNickname: String
)