package com.utkarsh.daggerhiltexample.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DatabaseRepository::class], version = 1)
abstract class RepositoriesDatabase : RoomDatabase() {

    abstract val repositoriesDatabaseDao: RepositoriesDatabaseDao

}
