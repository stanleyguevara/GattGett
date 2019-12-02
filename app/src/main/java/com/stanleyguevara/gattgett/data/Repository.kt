package com.stanleyguevara.gattgett.data

import androidx.lifecycle.LiveData
import com.stanleyguevara.gattgett.data.model.SyncStuff
import com.stanleyguevara.gattgett.data.persist.SyncStuffDatabase
import org.koin.core.KoinComponent
import org.koin.core.inject

class Repository : KoinComponent {

    private val database: SyncStuffDatabase by inject()

    suspend fun saveStuff(stuff: SyncStuff) {
        database.stuffDao().insertStuff(stuff)
    }

    fun getLastEntry(): LiveData<SyncStuff> {
        return database.stuffDao().getLast()
    }

    fun getEntryCount(): LiveData<Int> {
        return database.stuffDao().getNotProcessedCount()
    }
}