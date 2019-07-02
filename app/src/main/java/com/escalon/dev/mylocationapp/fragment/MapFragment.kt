package com.escalon.dev.mylocationapp.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.*
import android.widget.Toast
import com.escalon.dev.mylocationapp.MyLocationApplication.Companion.preferences
import com.escalon.dev.mylocationapp.R
import com.escalon.dev.mylocationapp.activity.login.LoginActivity
import com.escalon.dev.mylocationapp.network.NetworkManager
import com.escalon.dev.mylocationapp.network.repository.db.AppLocationDb
import com.escalon.dev.mylocationapp.network.repository.repo.AccessTokenRepository
import com.escalon.dev.mylocationapp.util.PrefUtils
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_map_view.*


class MapFragment : Fragment(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private var googleMap: GoogleMap? = null
    private var googleApiClient: GoogleApiClient? = null
    private var mapFragment: SupportMapFragment? = null
    private var locationManager: LocationManager? = null
    private var lastKnownLocation: Location? = null
    private var locationRequest: LocationRequest? = null
    private var currentLocationMarker: Marker? = null
    private var currentlocation: String? = null

    var accessTokenRepository: AccessTokenRepository? = null

    companion object {
        const val PERMISSIONS_REQUEST_LOCATION = 5

        fun getInstance() = MapFragment()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_map_view, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // set map screen as default whenever the user enters here
        preferences?.currentScreen = PrefUtils.MAP_SCREEN

        mapFragment = childFragmentManager?.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        fab_send_new_message.setOnClickListener {
            SendLocationMessageDialogFragment.getInstance(currentlocation).show(fragmentManager, "")
        }

        accessTokenRepository = AccessTokenRepository(
            AppLocationDb.getInstance(context).accessTokenDao(), NetworkManager.getNetworkManager()
        )
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

                && ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap?) {
        googleMap = map
        googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap?.uiSettings?.isZoomGesturesEnabled = true
        googleMap?.uiSettings?.isCompassEnabled = true
        googleMap?.uiSettings?.isMyLocationButtonEnabled = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && hasLocationPermission()) {
            googleMap?.isMyLocationEnabled = true
            initGooglePlayServices()
        } else {
            requestPermission()
        }
    }

    override fun onLocationChanged(location: Location?) {
        lastKnownLocation = location
        currentLocationMarker?.remove()

        // get accurate current location
        getMyCurrentLocation(location)
    }

    @SuppressLint("MissingPermission")
    private fun getMyCurrentLocation(location: Location?) {
        locationManager = activity?.getSystemService(LOCATION_SERVICE) as? LocationManager
        lastKnownLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        location?.let {
            val latLng = LatLng(it.latitude, it.longitude)

            // parameters need to be updated every time new location changes
            currentlocation = latLng.toString()

            currentLocationMarker = googleMap?.addMarker(
                MarkerOptions().position(latLng)
                    .snippet("Tu ubicacion actual!")
                    .icon(BitmapDescriptorFactory.defaultMarker())
            )

            googleMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            googleMap?.animateCamera(CameraUpdateFactory.zoomTo(16f))

            googleApiClient?.let { apiClient ->
                LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this)
            }
        }
    }

    @Synchronized
    private fun initGooglePlayServices() {
        googleApiClient = GoogleApiClient.Builder(activity)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        googleApiClient?.connect()
    }

    @SuppressLint("MissingPermission")
    override fun onConnected(p0: Bundle?) {

        if (hasLocationPermission()) {
            // request location updates
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, getLocationRequest(), this)
        } else {
            requestPermission()
        }

    }

    private fun getLocationRequest(): LocationRequest? {
        locationRequest = LocationRequest()
        locationRequest?.let {
            it.interval = 1000
            it.fastestInterval = 1000
            it.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
        return locationRequest
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {

            // show a message to alert user that requested permissions are required
            val alertDialog = AlertDialog.Builder(activity)
            alertDialog
                .setTitle("Requesting Permission")
                .setMessage("App needs to have the following permission to work properly")
                .setPositiveButton("OK") { _, _ ->
                    ActivityCompat.requestPermissions(
                        activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        PERMISSIONS_REQUEST_LOCATION
                    )
                }.create()
                .show()
        } else {
            Toast.makeText(activity, "permissions are required for app to work properly!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    if (ContextCompat.checkSelfPermission(
                            activity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                        // init google play services
                        googleMap?.isMyLocationEnabled = true
                        initGooglePlayServices()
                    }
                } else {
                    // permission denied
                    Toast.makeText(activity, "permissions are denied!, app won't work properly", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onConnectionSuspended(connectionSuspended: Int) {
        //Nop
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        Toast.makeText(activity, "Connection failed!", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.map_menu, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_logout) {
            // log user out
            performSignOut()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun performSignOut() {
        // clear preferences
        val pref = activity?.getSharedPreferences(PrefUtils.PREFS_FILENAME, Context.MODE_PRIVATE)
        pref?.edit()?.clear()?.apply()

        // clear token
        accessTokenRepository?.clearAccessToken()

        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}