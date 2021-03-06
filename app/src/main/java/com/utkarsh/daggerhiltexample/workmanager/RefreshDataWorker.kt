package com.utkarsh.daggerhiltexample.workmanager

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.utkarsh.daggerhiltexample.database.RepositoriesDatabaseDao
import com.utkarsh.daggerhiltexample.repository.TrendingReposRepository
import com.utkarsh.daggerhiltexample.utils.IS_CACHE_AVAILABLE
import com.utkarsh.daggerhiltexample.utils.defaultSharedPreferences
import com.utkarsh.daggerhiltexample.utils.putBoolean
import retrofit2.HttpException

class RefreshDataWorker @WorkerInject constructor(@Assisted private val appContext: Context,
                                                  @Assisted params: WorkerParameters,
                                                  private val dao: RepositoriesDatabaseDao,
                                                  private val repository: TrendingReposRepository):
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            //To discard cache
            dao.clear()
            appContext.defaultSharedPreferences.putBoolean(IS_CACHE_AVAILABLE, false)
            repository.refreshRepositories()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }

}