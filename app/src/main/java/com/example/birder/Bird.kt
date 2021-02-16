package com.example.birder

import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bird_table")
data class Bird(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var name: String,
    var description: String,
    val imageFilePath: String,
    val longitude: Double,
    val latitude: Double
)