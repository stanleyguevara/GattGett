package com.stanleyguevara.gattgett.data.persist

import androidx.lifecycle.LiveData
import androidx.room.*
import com.stanleyguevara.gattgett.data.model.SyncStuff

@Dao
interface SyncStuffDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertStuff(data: SyncStuff): Long

    @Query("SELECT * FROM sync_stuff ORDER BY read_timestamp DESC LIMIT 1")
    fun getLast(): LiveData<SyncStuff>

    @Query("SELECT count(*) FROM sync_stuff WHERE processed = 0")
    fun getNotProcessedCount(): LiveData<Int>

    @Query("SELECT * FROM sync_stuff WHERE processed = 0")
    suspend fun getNotProcessed(): List<SyncStuff>

    @Query("UPDATE sync_stuff SET processed = 1, processed_timestamp = :timestamp WHERE serial_number = :serial AND read_timestamp = :read")
    suspend fun setProcessed(timestamp: Long, serial: String, read: Long)

    @Query("DELETE FROM sync_stuff WHERE processed_timestamp < :processed OR read_timestamp < :read")
    suspend fun deleteOlderThan(processed: Long, read: Long = 0)
}