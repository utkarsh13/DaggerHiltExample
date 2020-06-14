package com.utkarsh.daggerhiltexample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.utkarsh.daggerhiltexample.R
import kotlinx.coroutines.*

class SplashActivity: AppCompatActivity() {

    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        delayedLaunch()
    }

    override fun onPause() {
        super.onPause()
        uiScope.cancel()
    }

    private fun delayedLaunch() {
        uiScope.launch {
            delay(500)

            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

