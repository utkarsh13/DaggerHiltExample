package com.utkarsh.daggerhiltexample.diHilt

import android.content.Context
import com.utkarsh.daggerhiltexample.database.RepositoriesDatabase
import com.utkarsh.daggerhiltexample.repository.TrendingReposRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ApplicationComponent::class)
object RepositoryModule {
    @Provides
    @ActivityRetainedScoped
    fun provideTrendingReposRepository(database: RepositoriesDatabase, applicationContext: Context): TrendingReposRepository {
        return TrendingReposRepository(database, applicationContext)
    }
}