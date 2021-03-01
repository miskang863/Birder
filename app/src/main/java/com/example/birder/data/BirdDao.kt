package com.example.birder.data

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BirdDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addBird(bird: Bird)

    @Query("SELECT * FROM bird_table ORDER BY id ASC")
    fun readAlldata(): LiveData<List<Bird>>

    @Update
    suspend fun updateBird(bird: Bird)

    @Delete
    suspend fun deleteBird(bird: Bird)

    @Query("SELECT * FROM bird_table WHERE name LIKE :searchQuery")
    fun searchDatabase(searchQuery: String): Flow<List<Bird>>
}