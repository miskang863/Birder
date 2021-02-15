package com.example.birder.fragments

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.birder.Bird
import com.example.birder.BirdListAdapter
import com.example.birder.MainActivity
import com.example.birder.R

class FavoritesFragment(
 //   private val listener: BirdListAdapter.OnItemClickListener,
    private val birds: MutableList<Bird>
) : Fragment() {

    lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val v = inflater.inflate(R.layout.fragment_favorites, container, false)

        recyclerView = v.findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(view?.context)

        recyclerView.adapter = BirdListAdapter(birds)
           // recyclerView.adapter = BirdListAdapter(birds, listener)

       val fab: View = v.findViewById(R.id.addFromFile)
        fab.setOnClickListener {
            val newFavoritesFragment = NewFavoritesFragment()
        val supportFragmentManager = childFragmentManager
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.favoritelayout, newFavoritesFragment)
            commit()
        }
        }

        return v
    }
}