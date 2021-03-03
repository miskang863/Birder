package com.example.birder.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BirdViewModel(application: Application) : AndroidViewModel(application) {

    val readAllData: LiveData<List<Bird>>
    private val repository: BirdRepository

    init {
        val birdDao = BirdDatabase.getDatabase(application).birdDao()
        repository = BirdRepository(birdDao)
        readAllData = repository.readAllData
    }

    fun addBird(bird: Bird) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addBird(bird)
        }
    }

    fun updateBird(bird: Bird) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateBird(bird)
        }
    }

    fun deleteBird(bird: Bird) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteBird(bird)
        }
    }

    fun searchDatabase(searchQuery: String): LiveData<List<Bird>> {
        return repository.searchDatabase(searchQuery).asLiveData()
    }
}