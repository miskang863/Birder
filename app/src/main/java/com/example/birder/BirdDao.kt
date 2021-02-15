package com.example.birder

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BirdDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addBird(bird: Bird)

    @Query("SELECT * FROM bird_table ORDER BY id ASC")
    fun readAlldata(): LiveData<List<Bird>>
}