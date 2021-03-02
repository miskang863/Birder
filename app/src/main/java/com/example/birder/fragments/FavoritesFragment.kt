package com.example.birder.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.birder.adapters.BirdListAdapter
import com.example.birder.data.BirdViewModel
import com.example.birder.R

class FavoritesFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var mBirdViewModel: BirdViewModel

    private lateinit var recyclerView: RecyclerView

    private val adapter = BirdListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val v = inflater.inflate(R.layout.fragment_favorites, container, false)
        setHasOptionsMenu(true)

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)

        val search = menu?.findItem(R.id.menu_search)
        val searchView = search?.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            searchDatabase(newText)
        }
        return true
    }

    private fun searchDatabase(query: String) {
        val searchQuery = "%$query%"

        mBirdViewModel.searchDatabase(searchQuery).observe(this, { list ->
            list.let {
                adapter.setData(it)
            }
        })
    }

}