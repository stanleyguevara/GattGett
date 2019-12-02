package com.stanleyguevara.gattgett.data.model

import androidx.room.Entity

// Naming could be better, but at this point it's just some 'stuff' from my POV :)
@Entity(tableName = "sync_stuff", primaryKeys = ["serial_number", "read_timestamp"])
data class SyncStuff(
    val serial_number: String,
    val battery_level: Int,
    val read_timestamp: Long,
    val processed: Boolean = false,
    val processed_timestamp: Long? = null
) {
    class Builder(
        private var serialNumber: String? = null,
        private var batteryLevel: Int? = null
    ) {

        fun serial(serial: String) = apply { this.serialNumber = serial }
        fun battery(battery: Int) = apply { this.batteryLevel = battery }
        fun build(): SyncStuff? {
            val serial = serialNumber
            val battery = batteryLevel
            return if (serial == null || battery == null) {
                null
            } else {
                SyncStuff(serial, battery, System.currentTimeMillis())
            }
        }
    }
}