package com.example.birder.adapters

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.birder.R
import com.example.birder.data.Bird
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowAdapter(
    context: Context,
    private val birdMap: MutableMap<String, String> = mutableMapOf()
) : GoogleMap.InfoWindowAdapter {

    lateinit var bird: Bird
    private var mWindow: View =
        LayoutInflater.from(context).inflate(R.layout.custom_info_window, null)

    private lateinit var duckSound: MediaPlayer


    private fun renderWindowText(marker: Marker, view: View) {
        val title = marker.title
        val titleTextview = view.findViewById<TextView>(R.id.markerTitle)

        if (title != null) {
            titleTextview.text = title
        }

        val snippet = marker.snippet
        val snippetTextview = view.findViewById<TextView>(R.id.markerSnippet)

        if (snippet != null) {
            snippetTextview.text = snippet
        }


        val imageView = view.findViewById<ImageView>(R.id.markerImage)
        Log.d("testi", "displaying marker id ${marker.id}")
        val birdUri = birdMap.getValue(marker.id)
        imageView.setImageURI(Uri.parse(birdUri))

        duckSound = MediaPlayer.create(view.context, R.raw.duck)

        val soundButton: Button = view.findViewById(R.id.btn_sound)

        soundButton.setOnClickListener {
            duckSound.start()
        }
    }

    override fun getInfoWindow(p0: Marker?): View {
        if (p0 != null) {
            renderWindowText(p0, mWindow)
        }
        return mWindow
    }

    override fun getInfoContents(p0: Marker?): View {
        if (p0 != null) {
            renderWindowText(p0, mWindow)
        }
        return mWindow
    }

}