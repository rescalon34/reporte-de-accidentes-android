package com.fisherman.core.repository.util

import android.util.Log
import com.escalon.dev.mylocationapp.network.repository.util.ApiError
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.Response
import java.util.regex.Pattern

/**
 * Common class used by API responses.
 * @param <T> the type of the response object
 */
@Suppress("unused") // T is used in extending classes
sealed class ApiResponse<T> {
    companion object {
        const val RESPONSE_CODE_202 = 202
        const val RESPONSE_CODE_404 = 404
        const val RESPONSE_CODE_401 = 401
        private const val RESPONSE_CODE_204 = 204
        private const val RESPONSE_CODE_500 = 500

        fun <T> create(error: Throwable): ApiErrorResponse<T> {
            return ApiErrorResponse(error.message ?: "unknown error", RESPONSE_CODE_500)
        }

        fun <T> create(response: Response<T>): ApiResponse<T> {
            return if (response.isSuccessful) {
                val body = response.body()
                if (body == null || response.code() == RESPONSE_CODE_204) {
                    ApiEmptyResponse(response.code())
                } else {
                    ApiSuccessResponse(
                        body = body,
                        linkHeader = response.headers()?.get("link"),
                        code = response.code()
                    )
                }
            } else {
                val msg = response.errorBody()?.string()
                val errorMsg = if (msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    msg
                }
                ApiErrorResponse(errorMsg ?: "unknown error", response.code())
            }
        }
    }
}

/**
 * separate class for HTTP 204 resposes so that we can make ApiSuccessResponse's body non-null.
 */
class ApiEmptyResponse<T>(val responseCode: Int) : ApiResponse<T>()

data class ApiSuccessResponse<T>(
    val body: T,
    val links: Map<String, String>,
    val responseCode: Int
) : ApiResponse<T>() {
    constructor(body: T, linkHeader: String?, code: Int) : this(
        body = body,
        links = linkHeader?.extractLinks() ?: emptyMap(),
        responseCode = code
    )

    val nextPage: Int? by lazy(LazyThreadSafetyMode.NONE) {
        links[NEXT_LINK]?.let { next ->
            val matcher = PAGE_PATTERN.matcher(next)
            if (!matcher.find() || matcher.groupCount() != 1) {
                null
            } else {
                try {
                    Integer.parseInt(matcher.group(1))
                } catch (ex: NumberFormatException) {
                    Log.d(ApiEmptyResponse::class.java.name, "cannot parse next page")
                    null
                }
            }
        }
    }

    companion object {
        private val LINK_PATTERN = Pattern.compile("<([^>]*)>[\\s]*;[\\s]*rel=\"([a-zA-Z0-9]+)\"")
        private val PAGE_PATTERN = Pattern.compile("\\bpage=(\\d+)")
        private const val NEXT_LINK = "next"

        private fun String.extractLinks(): Map<String, String> {
            val links = mutableMapOf<String, String>()
            val matcher = LINK_PATTERN.matcher(this)

            while (matcher.find()) {
                val count = matcher.groupCount()
                if (count == 2) {
                    links[matcher.group(2)] = matcher.group(1)
                }
            }
            return links
        }
    }
}

data class ApiErrorResponse<T>(val errorMessage: String, val responseCode: Int) : ApiResponse<T>() {
    fun defaultErrorMessages(): String {
        return try {
            val parsedErrorMessage = Gson().fromJson<ApiError>(errorMessage, ApiError::class.java)
            if (!parsedErrorMessage.error_description.isNullOrEmpty()) {
                parsedErrorMessage.error_description ?: ""
            } else if (!parsedErrorMessage.messageError.isNullOrEmpty()) {
                parsedErrorMessage.messageError ?: ""
            } else {
                var concatenatedErrors = ""
                parsedErrorMessage.errors.forEach {
                    concatenatedErrors += it.defaultMessage + "\n"
                }
                concatenatedErrors
            }
        } catch (ex: JsonSyntaxException) {
            ""
        }
    }
}