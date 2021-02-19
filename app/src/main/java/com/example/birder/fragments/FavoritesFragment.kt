package com.example.birder.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.birder.BirdListAdapter
import com.example.birder.BirdViewModel
import com.example.birder.R

class FavoritesFragment : Fragment() {

    private lateinit var mBirdViewModel: BirdViewModel

    private lateinit var recyclerView: RecyclerView

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
        mBirdViewModel.readAllData.observe(viewLifecycleOwner, { bird ->
            adapter.setData(bird)
        })


        val fab: View = v.findViewById(R.id.addFromFile)
        fab.setOnClickListener {
            val addFavoritesFragment = AddFavoritesFragment()
            val supportFragmentManager = childFragmentManager
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.favoritelayout, addFavoritesFragment)
                commit()
            }
        }
        container?.removeAllViews()
        return v
    }
}