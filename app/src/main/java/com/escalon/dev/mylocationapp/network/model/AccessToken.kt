package com.escalon.dev.mylocationapp.network.model

import android.arch.persistence.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["token"])
data class AccessToken(
    @field:SerializedName("token")
    val token: String,
    @field:SerializedName("success")
    var success: Boolean?
)