package com.escalon.dev.mylocationapp

import android.app.Application
import com.escalon.dev.mylocationapp.util.PrefUtils
import com.google.android.gms.maps.MapsInitializer

class MyLocationApplication : Application() {

    companion object {

        lateinit var instance: MyLocationApplication
        var preferences: PrefUtils? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        MapsInitializer.initialize(this)
        preferences = PrefUtils(applicationContext)

    }
}