package com.escalon.dev.mylocationapp.network.repository.util

import android.arch.lifecycle.LiveData
import com.fisherman.core.repository.util.ApiResponse
import com.fisherman.core.repository.util.LiveDataCallAdapter
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * A Retrofit adapter that converts the Call into a LiveDataCallAdapterFactory of ApiResponse.
 */
class LiveDataCallAdapterFactory : CallAdapter.Factory() {

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        if (CallAdapter.Factory.getRawType(returnType) is LiveData<*>) {
            return null
        }
        val observableType = CallAdapter.Factory.getParameterUpperBound(0, returnType as ParameterizedType)
        val rawObservableType = CallAdapter.Factory.getRawType(observableType)
        if (rawObservableType != ApiResponse::class.java) {
            throw IllegalArgumentException("type must be a resource")
        }
        if (observableType !is ParameterizedType) {
            throw IllegalArgumentException("resource must be parameterized")
        }
        val bodyType = CallAdapter.Factory.getParameterUpperBound(0, observableType)
        return LiveDataCallAdapter(bodyType)
    }
}
