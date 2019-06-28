package com.escalon.dev.mylocationapp.network

import com.escalon.dev.mylocationapp.network.model.AccessToken
import com.escalon.dev.mylocationapp.network.repository.util.LiveDataCallAdapterFactory
import com.escalon.dev.mylocationapp.network.repository.util.NullOrEmptyConverterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class NetworkManager {

    companion object {
        const val BASE_URL = "https://searchme-location.herokuapp.com/"
        private const val HEADER_AUTHORIZATION = "Authorization"

        fun getNetworkManager(accessToken: AccessToken? = null) = providedRetrofit(accessToken)

        private fun encodeAuthorization(accessToken: AccessToken?) = """${accessToken?.token}"""

        private fun providedRetrofit(accessToken: AccessToken? = null): LocationApi {
            val okHttpClientBuilder = OkHttpClient.Builder()

            okHttpClientBuilder.addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                accessToken?.let { requestBuilder.header(HEADER_AUTHORIZATION, encodeAuthorization(it)) }
                chain.proceed(requestBuilder.build())
            }

            val retrofitBuilder = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .addConverterFactory(NullOrEmptyConverterFactory())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClientBuilder.build())

            return retrofitBuilder.build().create(LocationApi::class.java)
        }
    }
}