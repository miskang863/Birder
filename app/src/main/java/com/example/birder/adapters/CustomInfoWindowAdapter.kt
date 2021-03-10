package com.example.birder.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import com.example.birder.R
import com.example.birder.data.Bird
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowAdapter(
    context: Context,
    private var birdMap: MutableMap<String, String> = mutableMapOf()
) : GoogleMap.InfoWindowAdapter {

    lateinit var bird: Bird

    @SuppressLint("InflateParams")
    private var mWindow: View =
        LayoutInflater.from(context).inflate(R.layout.custom_info_window, null)


    private fun renderWindowText(marker: Marker, view: View) {
        val title = marker.title
        val titleTextView = view.findViewById<TextView>(R.id.markerTitle)
        val imageView = view.findViewById<ImageView>(R.id.markerImage)
        val audioLogoImageView = view.findViewById<ImageView>(R.id.markerAudioLogo)



        if (title != null) {
            titleTextView.text = title

            if (title.contains("tit", true) || title.contains(
                    "duck",
                    true
                ) || title.contains("crow", true)
            ) {
                audioLogoImageView.visibility = VISIBLE
            } else {
                audioLogoImageView.visibility = INVISIBLE
            }
        }

        val snippet = marker.snippet
        val snippetTextView = view.findViewById<TextView>(R.id.markerSnippet)

        if (snippet != null) {
            snippetTextView.text = snippet
        }

        val birdUri = birdMap.getValue(marker.id)
        imageView.setImageURI(Uri.parse(birdUri))
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