package com.fb.myapplication

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class BondExpiryService : Service() {

    companion object {
        private const val WORK_NAME = "bond_expiry_check"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("BondExpiryService", "Service starting")
        
        // Define work constraints
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Create periodic work request
        val periodicWorkRequest = PeriodicWorkRequestBuilder<BondExpiryCheckWorker>(
            1, TimeUnit.DAYS // Check once per day
        )
        .setConstraints(constraints)
        .build()

        // Enqueue unique periodic work
        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE, // Replace existing if any
                periodicWorkRequest
            )

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
} 