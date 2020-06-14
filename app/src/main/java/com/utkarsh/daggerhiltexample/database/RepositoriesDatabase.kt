package com.utkarsh.daggerhiltexample.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DatabaseRepository::class], version = 1)
abstract class RepositoriesDatabase : RoomDatabase() {

    abstract val repositoriesDatabaseDao: RepositoriesDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: RepositoriesDatabase? = null

        fun getInstance(context: Context): RepositoriesDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RepositoriesDatabase::class.java,
                        "repositories_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
