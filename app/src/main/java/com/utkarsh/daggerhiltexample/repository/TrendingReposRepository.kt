package com.utkarsh.daggerhiltexample.repository

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.utkarsh.daggerhiltexample.database.DatabaseRepository
import com.utkarsh.daggerhiltexample.database.RepositoriesDatabase
import com.utkarsh.daggerhiltexample.database.RepositoriesDatabaseDao
import com.utkarsh.daggerhiltexample.database.asDomainModel
import com.utkarsh.daggerhiltexample.domain.Repository
import com.utkarsh.daggerhiltexample.network.RepositoriesApiService
import com.utkarsh.daggerhiltexample.network.asDBModel
import com.utkarsh.daggerhiltexample.trendingRepositories.RepositoriesApiStatus
import com.utkarsh.daggerhiltexample.trendingRepositories.RepositorySort
import com.utkarsh.daggerhiltexample.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class TrendingReposRepository @Inject constructor(
    private val dao: RepositoriesDatabaseDao,
    private val repositoriesApiService: RepositoriesApiService,
    private val context: Context
) {

    val repositories: LiveData<List<Repository>> = Transformations.map(dao.getAllRepositories()) {
        it.asDomainModel()
    }

    private val _apiStatus = MutableLiveData<RepositoriesApiStatus>()

    val apiStatus: LiveData<RepositoriesApiStatus>
        get() = _apiStatus

    suspend fun refreshRepositories() {
        val isConnected = checkForInternetConnection()
        val isCacheAvailable = getCacheStatus()

        if (!isConnected && isCacheAvailable) {
            _apiStatus.postValue(RepositoriesApiStatus.OFFLINE)
        } else {
            withContext(Dispatchers.IO) {
                try {
                    _apiStatus.postValue(RepositoriesApiStatus.LOADING)
                    val getRepositoriesDeferred = repositoriesApiService.getAllRepositoriesAsync()
                    val repositories = getRepositoriesDeferred.await()

                    dao.insertAllRepositories(repositories.asDBModel())
                    context.defaultSharedPreferences.putBoolean(IS_CACHE_AVAILABLE, true)
                    context.defaultSharedPreferences.putLong(LAST_CACHE_TIME, System.currentTimeMillis())
                    _apiStatus.postValue(RepositoriesApiStatus.SUCCESS)
                } catch (e: Exception) {
                    _apiStatus.postValue(RepositoriesApiStatus.ERROR)
                }
            }
        }
    }

    private fun getCacheStatus(): Boolean {
        val isCacheAvailable = context.defaultSharedPreferences.getBoolean(IS_CACHE_AVAILABLE, false)
        val lastCacheTime = context.defaultSharedPreferences.getLong(LAST_CACHE_TIME, 0)
        val isCacheGood = isCacheGood(lastCacheTime, System.currentTimeMillis())
        return isCacheAvailable && isCacheGood
    }

    private fun checkForInternetConnection(): Boolean {
        val connectivityManager= context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo=connectivityManager.activeNetworkInfo
        return  networkInfo!=null && networkInfo.isConnected
    }

    suspend fun getRepositories(sortType: RepositorySort): List<Repository> {
        return withContext(Dispatchers.IO) {
            val repos: List<DatabaseRepository> = when (sortType) {
                RepositorySort.SORT_NAME -> dao.getAllRepositoriesSortedByName()
                RepositorySort.SORT_STAR -> dao.getAllRepositoriesSortedByStars()
                else -> dao.getAllRepositoriesList()
            }
            repos.asDomainModel()
        }
    }
}