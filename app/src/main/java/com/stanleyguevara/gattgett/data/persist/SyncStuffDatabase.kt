package com.stanleyguevara.gattgett.data.persist

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.stanleyguevara.gattgett.data.model.SyncStuff

@Database(entities = [SyncStuff::class], version = 1, exportSchema = false)
abstract class SyncStuffDatabase : RoomDatabase() {

    abstract fun stuffDao(): SyncStuffDao

    companion object {
        @Volatile
        private var INSTANCE: SyncStuffDatabase? = null

        fun getInstance(context: Context): SyncStuffDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(
                        context
                    ).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                SyncStuffDatabase::class.java,
                "stuff.db"
            ).build()
    }

}