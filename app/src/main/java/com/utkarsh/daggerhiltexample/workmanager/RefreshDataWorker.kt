package com.utkarsh.daggerhiltexample.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.utkarsh.daggerhiltexample.database.RepositoriesDatabase
import com.utkarsh.daggerhiltexample.repository.TrendingReposRepository
import com.utkarsh.daggerhiltexample.utils.IS_CACHE_AVAILABLE
import com.utkarsh.daggerhiltexample.utils.defaultSharedPreferences
import com.utkarsh.daggerhiltexample.utils.putBoolean
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Payload {
        val database = RepositoriesDatabase.getInstance(applicationContext)
        val repository = TrendingReposRepository(database, applicationContext)

        try {
            //To discard cache
            database.repositoriesDatabaseDao.clear()
            applicationContext.defaultSharedPreferences.putBoolean(IS_CACHE_AVAILABLE, false)
            repository.refreshRepositories()
            return Payload(Result.SUCCESS)
        } catch (e: HttpException) {
            return Payload(Result.RETRY)
        }
    }

}