package com.stanleyguevara.gattgett.data.model

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import com.sensorberg.motionlessaverage.MotionlessAverage

class ScannedDevice(val mac: String, val source: BluetoothDevice) {
    var lastSeen: Long = 0
    var lastScan: ScanResult? = null
    var averageRssi: Float = 0f
    var rssiAverager: MotionlessAverage =
        MotionlessAverage.Builder.createTimeDependentAverage(1f, 2f, 1000L, 10000L)

    fun update(scan: ScanResult): ScannedDevice {
        lastScan = scan
        lastSeen = System.currentTimeMillis()
        averageRssi = rssiAverager.average(scan.rssi.toFloat())
        return this
    }
}