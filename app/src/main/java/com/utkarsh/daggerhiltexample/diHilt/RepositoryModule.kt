package com.utkarsh.daggerhiltexample.diHilt

import android.app.Application
import android.content.Context
import com.utkarsh.daggerhiltexample.database.RepositoriesDatabaseDao
import com.utkarsh.daggerhiltexample.network.RepositoriesApiService
import com.utkarsh.daggerhiltexample.repository.TrendingReposRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideTrendingReposRepository(dao: RepositoriesDatabaseDao, repositoriesApiService: RepositoriesApiService, @ApplicationContext context: Context): TrendingReposRepository {
        return TrendingReposRepository(dao, repositoriesApiService, context)
    }
}