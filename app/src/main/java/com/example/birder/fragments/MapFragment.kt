package com.example.birder.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.birder.data.Bird
import com.example.birder.data.BirdViewModel
import com.example.birder.adapters.CustomInfoWindowAdapter
import com.example.birder.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

import java.util.*

class MapFragment : Fragment(), OnMapReadyCallback {
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private lateinit var map: GoogleMap
    private val locationPermissionRequest = 99
    private lateinit var mBirdViewModel: BirdViewModel
    private var birdList = emptyList<Bird>()
    private var firstLoad = true
    private lateinit var infoAdapter: CustomInfoWindowAdapter
    private val birdUriMarkerMap = mutableMapOf<String, String>()
    private var markerArray: Array<Marker> = emptyArray()


    private fun getAddress(lat: Double?, lng: Double?): String {

        val geoCoder = Geocoder(activity, Locale.getDefault())
        val list = geoCoder.getFromLocation(lat ?: 0.0, lng ?: 0.0, 1)
        return list[0].getAddressLine(0)
    }

    //Checks for permission to use GPS
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
                    locationPermissionRequest
                )
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == locationPermissionRequest) {
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

        val mapFragment = supportFragmentManager.findFragmentById(R.id.myMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(v.context)

        //Get birds from DB
        mBirdViewModel = ViewModelProvider(this).get(BirdViewModel::class.java)
        mBirdViewModel.readAllData.observe(viewLifecycleOwner, { bird ->
            run {
                //Removes all markers
                markerArray.forEach {
                    it.remove()
                }

                birdList = bird
                addMarkers()
            }
        })
        return v
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun addMarkers() {
        val markerIconBitmap = BitmapFactory.decodeResource(context?.resources, R.drawable.owl)
        val resizeMarkerIconBitmap = Bitmap.createScaledBitmap(markerIconBitmap, 120, 120, true)

        birdList.forEach { Bird ->
            val latLng = LatLng(Bird.latitude, Bird.longitude)
            val markerOptions = MarkerOptions().position(latLng)
                .title(Bird.name)
                .snippet(
                    "${Bird.description}\n" +
                            "Y: ${"%.4f".format(latLng.latitude)} X: ${
                                "%.4f".format(latLng.longitude)
                            }\n" +
                            getAddress(latLng.latitude, latLng.longitude)
                )
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMarkerIconBitmap))

            val marker = map.addMarker(markerOptions)
            markerArray += marker
            birdUriMarkerMap[marker.id] = Bird.imageUri

            val duckSound = MediaPlayer.create(activity, R.raw.duck)
            val crowSound = MediaPlayer.create(activity, R.raw.crow)
            val yellowSound = MediaPlayer.create(activity, R.raw.yellowbird)

            //Set up bird sounds on infowindows
            map.setOnInfoWindowClickListener {
                when {
                    it.title.contains("tit", true) -> {
                        yellowSound.start()
                    }
                    it.title.contains("duck", true) -> {
                        duckSound.start()
                    }
                    it.title.contains("crow", true) -> {
                        crowSound.start()
                    }
                }
            }
            //Set up custom info windows
            infoAdapter = CustomInfoWindowAdapter(requireContext(), birdUriMarkerMap)
            map.setInfoWindowAdapter(infoAdapter)
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        getLocationAccess()
        getLocationUpdates()
    }

    private fun getLocationUpdates() {
        if (locationRequest != null) {
            return
        }
        locationRequest = LocationRequest()
        locationRequest!!.interval = 5000
        locationRequest!!.fastestInterval = 5000
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        locationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult) {
                // map.clear()
                if (locationResult.locations.isNotEmpty()) {
                    // Gets the items from bird list and presents them on the map as markers with clickable details


                    // Zooms in when opening the map
                    val lastLocation = LatLng(
                        locationResult.lastLocation.latitude,
                        locationResult.lastLocation.longitude
                    )
                    if (firstLoad) {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 11f))
                        firstLoad = false
                    }
                }
            }
        }
    }


    private fun startLocationUpdates() {
        if (activity?.let {
                ActivityCompat.checkSelfPermission(
                    it, Manifest.permission.ACCESS_FINE_LOCATION
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
        fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, null)
    }


}