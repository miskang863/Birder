package com.example.birder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.birder.fragments.*

import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(){

    private val birds: MutableList<Bird> = GlobalModel.birds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFragment = HomeFragment()
        val favoritesFragment = FavoritesFragment()
        val mapFragment = MapFragment()
        val birdSearchFragment = BirdSearchFragment()
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        makeCurrentFragment(homeFragment)

        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.ic_home -> makeCurrentFragment(homeFragment)
                R.id.ic_favorite -> makeCurrentFragment(favoritesFragment)
                R.id.ic_map -> makeCurrentFragment(mapFragment)
                R.id.ic_search -> makeCurrentFragment(birdSearchFragment)
            }
            true
        }

    }

    private fun makeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
    }

}