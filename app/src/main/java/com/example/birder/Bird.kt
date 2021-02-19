package com.example.birder

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bird_table")
data class Bird(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var name: String,
    var description: String,
    var imageUri: String,
    val longitude: Double,
    val latitude: Double,
    val time: String
)