package com.example.birder.fragments

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.birder.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File

private const val FILE_NAME = "photo.jpg"
private lateinit var photoFile: File
private const val REQUEST_CODE = 42
private const val gallery_image_code = 100


class NewFavoritesFragment : Fragment() {
    private lateinit var mBirdViewModel: BirdViewModel
    private val gallery_image_code = 100
    private var imageUri: Uri? = null
    lateinit var imageView: ImageView
    lateinit var bitmap: Bitmap
    lateinit var editText1: EditText
    lateinit var editText2: EditText

    val RequestPermissionCode = 1
    var mLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_newfavorites, container, false)

        mBirdViewModel = ViewModelProvider(this).get(BirdViewModel::class.java)
        editText1 = view.findViewById<EditText>(R.id.editName)
        editText2 = view.findViewById<EditText>(R.id.editDesc)
        imageView = view.findViewById(R.id.imageView)
        val cameraButton = view.findViewById<Button>(R.id.cameraButton)
        val button: Button = view.findViewById(R.id.galleryButton)
        val button2: Button = view.findViewById(R.id.btn_save)


        //Take photo with camera
        cameraButton.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FILE_NAME)

            val fileProvider = FileProvider.getUriForFile(
                view.context,
                "com.example.birder.fileprovider",
                photoFile
            )

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            if (takePictureIntent.resolveActivity(activity?.packageManager!!) != null) {
                startActivityForResult(takePictureIntent, REQUEST_CODE)
            } else {
                Toast.makeText(view.context, "Unable to open camera", Toast.LENGTH_SHORT).show()
            }
        }

        //Take photo from gallery
        button.setOnClickListener {

            photoFile = getPhotoFile(FILE_NAME)

            val fileProvider = FileProvider.getUriForFile(
                view.context,
                "com.example.birder.fileprovider",
                photoFile
            )

            val gallery = Intent(Intent.ACTION_PICK, fileProvider)
            startActivityForResult(gallery, gallery_image_code)
        }

        //Save to database
        button2.setOnClickListener {
            insertDataToDatabase()
        }
        container?.removeAllViews()

        fusedLocationClient = activity?.let { LocationServices.getFusedLocationProviderClient(it) }!!
        getLastLocation()
        return view
    }

    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = view?.context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Handle gallery image
        if (resultCode == RESULT_OK && requestCode == gallery_image_code) {
            imageUri = data?.data
            // bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, imageUri)
            imageView.setImageURI(imageUri)
        }

        //Handle camera image
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            val takenImage = BitmapFactory.decodeFile(photoFile.path)
            // Log.d("testi", photoFile.absolutePath)
            imageView.setImageBitmap(takenImage)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun insertDataToDatabase() {
        var name = editText1.text.toString()
        var desc = editText2.text.toString()

        // val uriPathHelper = URIPathHelper()
        //  val filePath = imageUri?.let { uriPathHelper.getPath(requireContext(), it) }
        val bird = Bird(0, name, desc, photoFile.absolutePath)

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
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    mLocation = location
                    if (location != null) {
                        println("DBG" + location.latitude + location.longitude)
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