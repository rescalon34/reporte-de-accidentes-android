package com.escalon.dev.mylocationapp.network.repository.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.escalon.dev.mylocationapp.network.model.AccessToken

@Dao
interface AccessTokenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAccessToken(accessToken: AccessToken)

    @Query("SELECT * FROM AccessToken LIMIT 1")
    fun getAccessToken(): LiveData<AccessToken>

    @Query("DELETE FROM AccessToken")
    fun clearAccessToken()
}