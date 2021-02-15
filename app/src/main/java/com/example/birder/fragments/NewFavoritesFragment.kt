package com.example.birder.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.birder.Bird
import com.example.birder.BirdViewModel
import com.example.birder.GlobalModel
import com.example.birder.R

class NewFavoritesFragment : Fragment() {
    private lateinit var mBirdViewModel: BirdViewModel
    private val pickImage = 100
    private var imageUri: Uri? = null
    lateinit var imageView: ImageView
    lateinit var bitmap: Bitmap
    lateinit var editText1: EditText
    lateinit var editText2: EditText


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
        editText1 = v.findViewById<EditText>(R.id.editName)
        editText2 = v.findViewById<EditText>(R.id.editDesc)

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

}