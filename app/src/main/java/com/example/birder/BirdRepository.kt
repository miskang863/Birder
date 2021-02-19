package com.example.birder

import androidx.lifecycle.LiveData

class BirdRepository(private val birdDao: BirdDao) {

    val readAllData: LiveData<List<Bird>> = birdDao.readAlldata()

    suspend fun addBird(bird: Bird) {
        birdDao.addBird(bird)
    }

    suspend fun updateBird(bird: Bird) {
        birdDao.updateBird(bird)
    }

    suspend fun deleteBird(bird: Bird) {
        birdDao.deleteBird(bird)
    }
}