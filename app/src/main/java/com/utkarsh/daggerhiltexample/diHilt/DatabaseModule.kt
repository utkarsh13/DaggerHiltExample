package com.utkarsh.daggerhiltexample.diHilt

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.utkarsh.daggerhiltexample.database.RepositoriesDatabase
import com.utkarsh.daggerhiltexample.database.RepositoriesDatabaseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideRepositoriesDatabase(@ApplicationContext context: Context): RepositoriesDatabase {
        return Room.databaseBuilder(
            context,
            RepositoriesDatabase::class.java,
            "repositories_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideRepositoriesDao(repositoriesDatabase: RepositoriesDatabase): RepositoriesDatabaseDao {
        return repositoriesDatabase.repositoriesDatabaseDao
    }
}
