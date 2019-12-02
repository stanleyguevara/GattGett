package com.stanleyguevara.gattgett

import android.app.Application
import androidx.work.WorkManager
import com.stanleyguevara.gattgett.sync.SyncWorker
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    val workManager: WorkManager by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(InjectionModule.module())
        }

        SyncWorker.schedule(workManager)
        //SyncWorker.testNow(workManager)
    }
}