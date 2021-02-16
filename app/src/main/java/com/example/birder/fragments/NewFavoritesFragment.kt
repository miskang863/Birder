package com.example.birder.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.birder.Bird
import com.example.birder.BirdViewModel
import com.example.birder.GlobalModel
import com.example.birder.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class NewFavoritesFragment : Fragment() {
    private lateinit var mBirdViewModel: BirdViewModel
    private val pickImage = 100
    private var imageUri: Uri? = null
    lateinit var imageView: ImageView
    lateinit var bitmap: Bitmap
    lateinit var editText1: EditText
    lateinit var editText2: EditText

    val RequestPermissionCode = 1
    var mLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_newfavorites, container, false)

        mBirdViewModel = ViewModelProvider(this).get(BirdViewModel::class.java)
        editText1 = v.findViewById(R.id.editName)
        editText2 = v.findViewById(R.id.editDesc)

        imageView = v.findViewById(R.id.imageView)
        val button: Button = v.findViewById(R.id.btn_file)
        button.setOnClickListener {
            val gallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        val button2: Button = v.findViewById(R.id.btn_save)
        button2.setOnClickListener {
            insertDataToDatabase()
        }
        container?.removeAllViews()

        fusedLocationProviderClient = activity?.let { LocationServices.getFusedLocationProviderClient(it) }!!
        getLastLocation()
        return v
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            // bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, imageUri)
            imageView.setImageURI(imageUri)
        }
    }

    private fun insertDataToDatabase() {
        var name = editText1.text.toString()
        var desc = editText2.text.toString()

        val bird = Bird(0, name, desc, imageUri.toString())

        mBirdViewModel.addBird(bird)
        Toast.makeText(requireContext(), "Bird added", Toast.LENGTH_LONG).show()
    }

    fun getLastLocation() {
        if (activity?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
        } else {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    mLocation = location
                    if (location != null) {

                      /*  latitude.text = location.latitude.toString()
                        longitude.text = location.longitude.toString()
                        time.text = android.text.format.DateFormat.getTimeFormat(applicationContext)
                            .format(location.time)
                        date.text =
                            android.text.format.DateFormat.getDateFormat(getApplicationContext())
                                .format(location.time) */
                    }
                }
        }
    }
    private fun requestPermission() {
        activity?.let {
            ActivityCompat.requestPermissions(
                it,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                RequestPermissionCode
            )
        }
    }
}