package com.escalon.dev.mylocationapp.network.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("name")
    val userName: String?,
    @SerializedName("password")
    val password: String?
)