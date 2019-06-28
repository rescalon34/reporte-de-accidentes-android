package com.escalon.dev.mylocationapp.network.repository.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.escalon.dev.mylocationapp.network.model.AccessToken

@Database(
    entities = [AccessToken::class],
    version = 1,
    exportSchema = false
)

abstract class AppLocationDb : RoomDatabase() {

    companion object {
        const val DB_NAME = "APP_LOCATION_DB"

        fun getInstance(context: Context): AppLocationDb {
            return Room.databaseBuilder(context, AppLocationDb::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun accessTokenDao(): AccessTokenDao
}