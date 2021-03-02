package com.example.birder

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.birder.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val favoritesFragment = FavoritesFragment()
        val mapFragment = MapFragment()
        val birdSearchFragment = BirdSearchFragment()
        val arFragment = ARCameraFragment()
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        makeCurrentFragment(favoritesFragment)

        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this@MainActivity,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                99
            )
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                99
            )
        }

        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.ic_favorite -> makeCurrentFragment(favoritesFragment)
                R.id.ic_map -> makeCurrentFragment(mapFragment)
                R.id.ic_search -> makeCurrentFragment(birdSearchFragment)
                R.id.ic_ar -> makeCurrentFragment(arFragment)
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