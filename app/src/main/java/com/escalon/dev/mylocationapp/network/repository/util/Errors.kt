package com.escalon.dev.mylocationapp.network.repository.util

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Errors(
    @SerializedName("defaultMessage")
    val defaultMessage: String,
    @SerializedName("objectName")
    val objectName: String,
    @SerializedName("field")
    val field: String
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: ""
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(defaultMessage)
        writeString(objectName)
        writeString(field)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Errors> = object : Parcelable.Creator<Errors> {
            override fun createFromParcel(source: Parcel): Errors = Errors(source)
            override fun newArray(size: Int): Array<Errors?> = arrayOfNulls(size)
        }
    }
}