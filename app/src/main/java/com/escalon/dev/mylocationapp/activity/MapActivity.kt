package com.escalon.dev.mylocationapp.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.escalon.dev.mylocationapp.R
import com.escalon.dev.mylocationapp.fragment.MapFragment

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        setContentFragmentScreen(MapFragment.getInstance())
    }

    private fun setContentFragmentScreen(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.base_fragment, fragment).commit()
        supportFragmentManager.executePendingTransactions()
    }
}
