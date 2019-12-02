package com.stanleyguevara.gattgett.sync

import android.content.Context
import android.util.Log
import androidx.work.*
import com.stanleyguevara.gattgett.data.persist.SyncStuffDatabase
import kotlinx.coroutines.delay
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.concurrent.TimeUnit

class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params), KoinComponent {

    private val TAG: String = SyncWorker::class.java.simpleName

    private val database: SyncStuffDatabase by inject()

    override suspend fun doWork(): Result {
        val pending = database.stuffDao().getNotProcessed()
        var success = true
        // Stuff here depends on how REST API looks on the other side
        // If it's single or few calls for all data then it can be done sequentially
        // If there's a lot of them then we'd need some parallelism
        // Also remove older than X days entries here
        pending.forEachIndexed { index, item ->
            Log.d(TAG, "Processing ${index + 1} / ${pending.size}")
            delay(200L) // Let's pretend it's network
            if (Math.random() < 0.1) {  // Let's pretend it fails sometimes
                success = false
            } else {
                database.stuffDao().setProcessed(System.currentTimeMillis(), item.serial_number, item.read_timestamp)
            }
        }
        val data = Data.Builder()
            .putLong(LAST_RUN_TIME, System.currentTimeMillis())
            .build()
        return if (success) {
            Result.success(data)
        } else {
            Result.failure(data)
        }
    }

    companion object {

        const val WORK_NAME = "SyncWorker"

        const val LAST_RUN_TIME = "last_run_time"

        fun testNow(workManager: WorkManager) {
            OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresCharging(true)
                        .setRequiredNetworkType(NetworkType.UNMETERED)
                        .build()
                ).build().apply {
                    workManager.enqueueUniqueWork(
                        WORK_NAME + "Test",
                        ExistingWorkPolicy.KEEP,
                        this
                    )
                }
        }

        fun schedule(workManager: WorkManager) {
            // In real life more like:
            // repeatInterval:  16, TimeUnit.HOURS,
            // flexInterval:    8,  TimeUnit.HOURS
            // Also it shouldn't really schedule, unless there are entries to sync in DB
            PeriodicWorkRequestBuilder<SyncWorker>(
                15, TimeUnit.MINUTES
            ).setConstraints(
                Constraints.Builder()
                    .setRequiresCharging(true)
                    .setRequiredNetworkType(NetworkType.UNMETERED)
                    .build()
            ).build().apply {
                workManager.enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    this
                )
            }
        }
    }
}