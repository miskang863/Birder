package com.example.birder.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.birder.R
import com.example.birder.data.Bird
import com.example.birder.data.BirdViewModel
import java.io.File

lateinit var imageView: ImageView
lateinit var editTextDesc: EditText
lateinit var editTextName: EditText
lateinit var cameraButton: Button
lateinit var galleryButton: Button
lateinit var updateButton: Button

private const val FILE_NAME = "photo.jpg"
private lateinit var photoFile: File
private const val REQUEST_CODE = 42
private const val gallery_image_code = 100
private const val requestPermissionCode = 1


private lateinit var mBirdViewModel: BirdViewModel

class SingleBirdFragment(var bird: Bird) : Fragment() {
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_single_bird, container, false)

        mBirdViewModel = ViewModelProvider(this).get(BirdViewModel::class.java)

        editTextName = view.findViewById(R.id.singleEditName)
        editTextDesc = view.findViewById(R.id.singleEditDesc)
        imageView = view.findViewById(R.id.singleImageView)
        cameraButton = view.findViewById(R.id.singleCameraButton)
        galleryButton = view.findViewById(R.id.singleGalleryButton)
        updateButton = view.findViewById(R.id.singleUpdateButton)

        editTextName.setText(bird.name)
        editTextDesc.setText(bird.description)
        imageView.setImageURI(Uri.parse(bird.imageUri))

        if (ContextCompat.checkSelfPermission(view.context, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED)
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.CAMERA), 11)
            }

        cameraButton.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FILE_NAME)

            val fileProvider = FileProvider.getUriForFile(
                view.context,
                "com.example.birder.fileprovider",
                photoFile
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            if (activity?.let { it1 -> takePictureIntent.resolveActivity(it1.packageManager) } != null) {
                startActivityForResult(takePictureIntent, REQUEST_CODE)
            } else {
                Toast.makeText(activity,"Unable to open camera", Toast.LENGTH_SHORT).show()
            }
        }

        //Take photo from gallery button
        galleryButton.setOnClickListener {
            photoFile = getPhotoFile(FILE_NAME)

            val fileProvider = FileProvider.getUriForFile(
                view.context,
                "com.example.birder.fileprovider",
                photoFile
            )
            val gallery = Intent(Intent.ACTION_OPEN_DOCUMENT, fileProvider)
            startActivityForResult(gallery, gallery_image_code)
        }

        updateButton.setOnClickListener {
            updateBird()
        }

        setHasOptionsMenu(true)

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
       // super.onActivityResult(requestCode, resultCode, data)
        //Handle gallery image
        if (resultCode == Activity.RESULT_OK && requestCode == gallery_image_code) {
            imageUri = data?.data
            imageUri?.let {
                data?.flags?.and(
                    (Intent.FLAG_GRANT_READ_URI_PERMISSION + Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                )?.let { it1 ->
                    context!!.contentResolver.takePersistableUriPermission(
                        it, it1
                    )
                }
            }
            bird.imageUri = imageUri.toString()
            imageView.setImageURI(imageUri)
        }

        //Handle camera image
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val takenImage = BitmapFactory.decodeFile(photoFile.path)
            imageUri = Uri.fromFile(photoFile)
            bird.imageUri = imageUri.toString()
            imageView.setImageBitmap(takenImage)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun updateBird() {
        val name = editTextName.text.toString()
        val description = editTextDesc.text.toString()

        val updateBird =
            Bird(
                bird.id,
                name,
                description,
                bird.imageUri,
                bird.longitude,
                bird.latitude,
                bird.time
            )

        if (name.isNotEmpty() && description.isNotEmpty()) {
            mBirdViewModel.updateBird(updateBird)
            Toast.makeText(requireContext(), "Bird updated!", Toast.LENGTH_LONG).show()

            val manager = (view?.context as FragmentActivity).supportFragmentManager
            val favoritesFragment = FavoritesFragment()
            manager.beginTransaction().apply {
                replace(R.id.fl_wrapper, favoritesFragment)
                commit()
            }
        } else {
            Toast.makeText(requireContext(), "Bird update failed!", Toast.LENGTH_LONG).show()
        }

    }

    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = view?.context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d("testi", "MENU OPTIONS")
        menu.clear()
        inflater.inflate(R.menu.delete_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_delete) {
            deleteBird()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteBird() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            mBirdViewModel.deleteBird(bird)
            Toast.makeText(requireContext(), "Removed ${bird.name}", Toast.LENGTH_SHORT).show()

            val favoritesFragment = FavoritesFragment()
            val manager = (view?.context as FragmentActivity).supportFragmentManager
            manager.beginTransaction().apply {
                replace(R.id.fl_wrapper, favoritesFragment)
                commit()
            }
        }
        builder.setNegativeButton("No") { _, _ ->

        }
        builder.setTitle("Delete ${bird.name}?")
        builder.setMessage("Are you sure you want to delete ${bird.name}?")
        builder.create().show()
    }

}
