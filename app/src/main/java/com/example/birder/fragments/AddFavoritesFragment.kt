package com.example.birder.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_OPEN_DOCUMENT
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.birder.R
import com.example.birder.data.Bird
import com.example.birder.data.BirdViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


private const val FILE_NAME = "photo.jpg"
private lateinit var photoFile: File
private const val REQUEST_CODE = 42


class AddFavoritesFragment : Fragment() {
    private lateinit var mBirdViewModel: BirdViewModel
    private val galleryImageCode = 100
    private var imageUri: Uri? = null
    private lateinit var imageView: ImageView
    private lateinit var editText1: EditText
    private lateinit var editText2: EditText

    private val requestPermissionCode = 1
    private var mLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_addfavorites, container, false)
        setHasOptionsMenu(true)

        mBirdViewModel = ViewModelProvider(this).get(BirdViewModel::class.java)
        editText1 = view.findViewById(R.id.editName)
        editText2 = view.findViewById(R.id.editDesc)
        imageView = view.findViewById(R.id.imageView)
        val cameraButton = view.findViewById<Button>(R.id.cameraButton)
        val button: Button = view.findViewById(R.id.galleryButton)
        val button2: Button = view.findViewById(R.id.btn_save)

        if (ContextCompat.checkSelfPermission(view.context, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED
        )
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION),
                    11
                )
            }

        //Take photo with camera button
        cameraButton.setOnClickListener {
            getLastLocation()
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FILE_NAME)

            val fileProvider = FileProvider.getUriForFile(
                view.context,
                "com.example.birder.fileprovider",
                photoFile
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            startActivityForResult(takePictureIntent, REQUEST_CODE)

        }

        //Take photo from gallery button
        button.setOnClickListener {
            requestPermission()
            getLastLocation()
            photoFile = getPhotoFile(FILE_NAME)

            val fileProvider = FileProvider.getUriForFile(
                view.context,
                "com.example.birder.fileprovider",
                photoFile
            )

            val gallery = Intent(ACTION_OPEN_DOCUMENT, fileProvider)
            startActivityForResult(gallery, galleryImageCode)
        }

        //Save to database
        button2.setOnClickListener {
            insertDataToDatabase()

            val fragment: Fragment = FavoritesFragment()
            val fragmentManager: FragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fl_wrapper, fragment)
            fragmentTransaction.commit()
        }

        fusedLocationClient =
            activity?.let { LocationServices.getFusedLocationProviderClient(it) }!!

        return view
    }

    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = view?.context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Handle gallery image
        if (resultCode == RESULT_OK && requestCode == galleryImageCode) {
            imageUri = data?.data
            imageUri?.let {
                data?.flags?.and(
                    (FLAG_GRANT_READ_URI_PERMISSION + Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                )?.let { it1 ->
                    context!!.contentResolver.takePersistableUriPermission(
                        it, it1
                    )
                }
            }
            imageView.setImageURI(imageUri)
        }

        //Handle camera image
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            val takenImage = BitmapFactory.decodeFile(photoFile.path)
            imageUri = Uri.fromFile(photoFile)
            imageView.setImageBitmap(takenImage)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertDataToDatabase() {
        val name = editText1.text.toString()
        val desc = editText2.text.toString()
        val time = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        val formattedTime = time.format(formatter)
        val bird =
            mLocation?.let {
                Bird(
                    0,
                    name,
                    desc,
                    imageUri.toString(),
                    it.longitude,
                    mLocation!!.latitude,
                    formattedTime
                )
            }

        if (bird != null) {
            mBirdViewModel.addBird(bird)
            Toast.makeText(requireContext(), "Bird added", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), "Adding bird failed", Toast.LENGTH_LONG).show()
        }
    }

    //GPS location
    private fun getLastLocation() {
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
                }
        }
    }

    private fun requestPermission() {
        activity?.let {
            ActivityCompat.requestPermissions(
                it,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                requestPermissionCode
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }
}