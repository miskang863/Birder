package com.example.birder.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.birder.MainActivity
import com.example.birder.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var map: GoogleMap
    private val LOCATION_PERMISSION_REQUEST = 99

    private fun getAddress(lat: Double?, lng: Double?): String {
        val geocoder = Geocoder(activity)
        val list = geocoder.getFromLocation(lat ?: 0.0, lng ?: 0.0, 1)
        return list[0].getAddressLine(0)
    }

    private fun getLocationAccess() {
        if (activity?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
            getLocationUpdates()
            startLocationUpdates()
        } else
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST
                )
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                getLocationAccess()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_map, container, false)

        val supportFragmentManager = childFragmentManager

        val mapFragment = supportFragmentManager?.findFragmentById(R.id.myMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(v.context)
        return v

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        getLocationAccess()
        getLocationUpdates()
    }

    private fun getLocationUpdates() {
        locationRequest = LocationRequest()
        locationRequest.interval = 20000
        locationRequest.fastestInterval = 15000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.locations.isNotEmpty()) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        val latLng = LatLng(location.latitude, location.longitude)
                        val markerOptions = MarkerOptions().position(latLng)
                            .title("Latitude and Longitude")
                            .snippet(
                                "Y: ${location.latitude} X: ${location.longitude}${
                                    getAddress(location.latitude, location.longitude)}"
                            )
                        map.addMarker(markerOptions)
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    }
                }
            }
        }
    }

    private fun startLocationUpdates() {
        if (activity?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } !=
            PackageManager.PERMISSION_GRANTED && activity?.let {
                ActivityCompat.checkSelfPermission(
                    it, Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } !=
            PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }


}