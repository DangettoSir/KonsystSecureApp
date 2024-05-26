package com.example.konsystsecureapp.network

import com.google.gson.annotations.SerializedName

data class EventResponse(
    @SerializedName("title") val title: String,
)