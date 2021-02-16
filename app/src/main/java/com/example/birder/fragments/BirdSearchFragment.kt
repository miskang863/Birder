package com.example.birder.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.example.birder.R
import com.example.birder.WikiApi
import com.example.birder.imageApi.BirdImageJson
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


const val BASE_URL = "https://en.wikipedia.org/w/"

class BirdSearchFragment : Fragment() {

    private var TAG = "BirdSearch"
    lateinit var birdNameTextView: TextView
    lateinit var birdDescriptionTextView: TextView
    lateinit var searchImageView: ImageView
    lateinit var searchImageButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bird_search, container, false)
        // Inflate the layout for this fragment
        val searchBtn = view.findViewById<Button>(R.id.searchButton)
        val editText = view.findViewById<EditText>(R.id.birdSearchEditText)

        searchBtn.setOnClickListener {
            if (editText.text.isNotEmpty()) {
                getBirdData(editText.text.toString())
            }
        }

        //Search when user hits Enter on keyboard
        editText.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                // if the event is a key down event on the enter button
                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    searchBtn.performClick()
                    hideKeyboard(view)
                    // clear focus and hide cursor from edit text
                    editText.clearFocus()
                    editText.isCursorVisible = false

                    return true
                }
                return false
            }
        })
        return view
    }

    //API calls and UI changes
    private fun getBirdData(birdSpecies: String) {

        if (view != null) {
            birdNameTextView = view!!.findViewById<TextView>(R.id.birdNameTextView)
            birdDescriptionTextView = view!!.findViewById<TextView>(R.id.birdDescriptionTextView)
            searchImageView = view!!.findViewById<ImageView>(R.id.searchImageView)
            searchImageButton = view!!.findViewById<ImageButton>(R.id.searchImageButton)
        }

        //API calls
        val call = WikiApi.service
        GlobalScope.launch(Dispatchers.IO) {
            val response: BirdImageJson? =
                call.bird("query", "json", 2, "pageimages|pageterms", "original", birdSpecies)

            val img = response?.query?.pages?.get(0)?.original?.source
                ?: "https://images.freeimages.com/images/large-previews/8e8/black-bird-1172941.jpg"

            val birdWikiUrl =
                "http://en.wikipedia.org/wiki?curid=${response?.query?.pages?.get(0)?.pageid}"

            var birdDescText = response?.query?.pages?.get(0)?.terms?.description.toString()
            birdDescText = birdDescText.replace("[", "").replace("]", "").capitalize(Locale.ROOT)

            if(birdDescText == "Null"){
                birdDescText = "Description not found"
            }
            //Handle the UI
            withContext(Dispatchers.Main) {
                birdDescriptionTextView.text = birdDescText
                if (response != null) {
                    birdNameTextView.text = response.query.pages[0].title
                }
                Picasso.get().load(img).into(searchImageView)
                searchImageButton.visibility = VISIBLE

                searchImageButton.setOnClickListener {
                    goToUrl(birdWikiUrl)
                }
            }
        }
    }

    //Open wikipedia page
    private fun goToUrl(url: String) {
        val uri = Uri.parse(url)
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    private fun hideKeyboard(view: View) {
        view?.apply {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


}