package com.example.birder

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Bird::class], version = 1, exportSchema = false)
abstract class BirdDatabase: RoomDatabase() {

    abstract fun birdDao(): BirdDao

    companion object{
        @Volatile
        private var INSTANCE: BirdDatabase? = null

        fun getDatabase(context: Context): BirdDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BirdDatabase::class.java,
                    "bird_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}