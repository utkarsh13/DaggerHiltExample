package com.utkarsh.daggerhiltexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.work.*
import com.utkarsh.daggerhiltexample.R
import com.utkarsh.daggerhiltexample.workmanager.RefreshDataWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val activityScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initWork()
    }

    private fun initWork() {
        activityScope.launch {
            setupRecurringWork()
        }
    }

    private fun setupRecurringWork() {
        val constraints = Constraints.Builder()
            .build()

        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(2, TimeUnit.SECONDS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            RefreshDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            repeatingRequest)

    }
}
