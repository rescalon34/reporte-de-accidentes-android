package com.escalon.dev.mylocationapp.network.repository.util

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Handle Api Errors
 */
data class ApiError(
    @SerializedName("statusCode")
    val statusCode: Int?,
    @SerializedName("error")
    val error: String?,
    @SerializedName("message")
    val messageError: String?,
    @SerializedName("error_description")
    val error_description: String?,
    @SerializedName("errors")
    val errors: List<Errors>
) : Parcelable {
    constructor() : this(
            0,
            "",
            "",
            "",
            listOf())

    constructor(source: Parcel) : this(
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString(),
            source.readString(),
            source.readString(),
            ArrayList<Errors>().apply { source.readList(this, Errors::class.java.classLoader) }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeValue(statusCode)
        writeString(error)
        writeString(error_description)
        writeString(messageError)
        writeList(errors)
    }

    companion object CREATOR : Parcelable.Creator<ApiError> {
        override fun createFromParcel(source: Parcel): ApiError = ApiError(source)
        override fun newArray(size: Int): Array<ApiError?> = arrayOfNulls(size)
    }
}