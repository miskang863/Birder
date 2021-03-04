package com.example.birder.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.birder.R
import com.example.birder.adapters.BirdListAdapter
import com.example.birder.data.BirdViewModel

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

        Log.d("testi", "NYT ON FAVEIS")

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
            val manager = (v.context as FragmentActivity).supportFragmentManager
            manager.beginTransaction().apply {
                replace(R.id.favoritelayout, addFavoritesFragment)
                addToBackStack(null)
                commit()
            }
        }
      //  container?.removeAllViews()
        return v
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.search_menu, menu)

        val search = menu.findItem(R.id.menu_search)
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