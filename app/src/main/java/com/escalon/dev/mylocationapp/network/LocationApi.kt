package com.escalon.dev.mylocationapp.network

import android.arch.lifecycle.LiveData
import com.escalon.dev.mylocationapp.network.model.AccessToken
import com.fisherman.core.repository.util.ApiResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LocationApi {

    @POST("api/authenticate")
    @FormUrlEncoded
    fun getAccessToken(
        @Field("name") username: String?,
        @Field("password") password: String?
    ): LiveData<ApiResponse<AccessToken>>


    @POST("api/register")
    @FormUrlEncoded
    fun registerUser(
        @Field("name") username: String?,
        @Field("password") password: String?
    ): LiveData<ApiResponse<Any>>


    @POST("api/location")
    @FormUrlEncoded
    fun sendLocationMessage(
        @Field("location") location: String?,
        @Field("message") message: String?
    ): LiveData<ApiResponse<Any>>
}