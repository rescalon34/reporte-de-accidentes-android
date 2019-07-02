package com.escalon.dev.mylocationapp.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.escalon.dev.mylocationapp.R
import com.escalon.dev.mylocationapp.fragment.MapFragment

class MapActivity : AppCompatActivity() {

    var mapFragment: MapFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapFragment = MapFragment.getInstance()
        mapFragment?.let {
            setContentFragmentScreen(it)
        }
    }

    private fun setContentFragmentScreen(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.base_fragment, fragment).commit()
        supportFragmentManager.executePendingTransactions()
    }

    /**
     * request permissions are thrown to Activity,
     * return same requested data to fragment in order to handle its proper logic
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mapFragment?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
