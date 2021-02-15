package com.example.birder.fragments

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.birder.*

class FavoritesFragment : Fragment() {

    private lateinit var mBirdViewModel: BirdViewModel

    lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val v = inflater.inflate(R.layout.fragment_favorites, container, false)

        val adapter = BirdListAdapter()
        recyclerView = v.findViewById(R.id.recyclerView)
      //  recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        mBirdViewModel = ViewModelProvider(this).get(BirdViewModel::class.java)
        mBirdViewModel.readAllData.observe(viewLifecycleOwner, Observer { bird ->
            adapter.setData(bird)
        })

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