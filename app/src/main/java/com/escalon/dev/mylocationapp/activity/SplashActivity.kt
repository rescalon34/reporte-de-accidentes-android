package com.escalon.dev.mylocationapp.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.escalon.dev.mylocationapp.MyLocationApplication.Companion.preferences
import com.escalon.dev.mylocationapp.util.PrefUtils.Companion.LOGIN_SCREEN
import com.escalon.dev.mylocationapp.util.PrefUtils.Companion.MAP_SCREEN
import com.escalon.dev.mylocationapp.R
import com.escalon.dev.mylocationapp.activity.login.LoginActivity
import java.util.*

class SplashActivity : AppCompatActivity() {

    companion object {
        private const val MINIMUM_SPLASH_DELAY = 1000.toLong()
    }

    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        handleNextScreenAction()
    }

    private fun handleNextScreenAction() {
        val task = object : TimerTask() {
            override fun run() {
                // route to appropriate screen depending on preferences param
                routeToAppropriateScreen()
            }
        }
        timer = Timer()
        timer?.schedule(task, MINIMUM_SPLASH_DELAY)
    }

    private fun routeToAppropriateScreen() {
        var intent: Intent? = null
        when (preferences?.currentScreen) {
            LOGIN_SCREEN -> intent = Intent(this, LoginActivity::class.java)
            MAP_SCREEN -> intent = Intent(this, MapActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}
