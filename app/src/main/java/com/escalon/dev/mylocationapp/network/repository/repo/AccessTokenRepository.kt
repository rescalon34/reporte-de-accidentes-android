package com.escalon.dev.mylocationapp.network.repository.repo

import com.escalon.dev.mylocationapp.network.LocationApi
import com.escalon.dev.mylocationapp.network.model.AccessToken
import com.escalon.dev.mylocationapp.network.model.CreateUserRequest
import com.escalon.dev.mylocationapp.network.model.LoginRequest
import com.escalon.dev.mylocationapp.network.repository.db.AccessTokenDao
import com.escalon.dev.mylocationapp.network.repository.util.AppExecutors

class AccessTokenRepository(
    private val accessTokenDao: AccessTokenDao,
    private val apiService: LocationApi,
    private val appExecutors: AppExecutors = AppExecutors()
) {

    /**
     * get access token by signing user in
     */
    fun getAccessToken(loginRequest: LoginRequest) = apiService.getAccessToken(
        loginRequest.userName, loginRequest.password
    )

    /**
     * register user with username / password credentials
     */
    fun registerUser(createUserRequest: CreateUserRequest) = apiService.registerUser(
        createUserRequest.userName, createUserRequest.password
    )

    /**
     * Get access token from database
     */
    fun queryAccessToken() = accessTokenDao.getAccessToken()

    /**
     * Insert a new access token in the database
     * @param accessToken the newest access token object
     */
    fun insertAccessTokenRepo(accessToken: AccessToken) {
        appExecutors.diskIO().execute {
            accessTokenDao.apply {
                clearAccessToken()
                insertAccessToken(accessToken)
            }
        }
    }

    /**
     * Clear access token from local database
     */
    fun clearAccessToken() {
        appExecutors.diskIO().execute {
            accessTokenDao.clearAccessToken()
        }
    }
}