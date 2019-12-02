package com.stanleyguevara.gattgett.scanner

import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.stanleyguevara.gattgett.data.AppState
import com.stanleyguevara.gattgett.data.Navigation
import com.stanleyguevara.gattgett.data.model.ScannedDevice
import com.stanleyguevara.gattgett.scanner.Scanner.State.*
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * This is simplistic scanner but should be sufficient for purpose of this task
 * In real world there's quite a bit more to unpack in a scanner, e.g.
 * - Starting / stopping scanning as scanning efficiency is better on some devices
 * - Counting start/stops command in moving time window to avoid Nougat punishing excessive scanning
 * - Handling ezoteric bugs where the whole BLE stack crashes
 * - If API is on the lower side it would be good to use nordic compat scanner lib,
 *   but since filtering / batching is not needed here I decided against it
 */
class ScannerImpl(private val scanner: BluetoothLeScanner) : Scanner, KoinComponent {

    private val TAG: String = ScannerImpl::class.java.simpleName

    private val navigation: Navigation by inject()
    private val appState: AppState by inject()

    private val handler = Handler(Looper.getMainLooper())
    private val disptach = Runnable {
        dispatch()
    }

    private val lock = Any()
    private val devices = mutableMapOf<String, ScannedDevice>()
    private val state = MediatorLiveData<Scanner.State>()
    private val data = object : MediatorLiveData<List<ScannedDevice>>() {
        override fun onActive() {
            super.onActive()
            evalState()
        }

        override fun onInactive() {
            super.onInactive()
            evalState()
        }
    }

    private val callback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, scan: ScanResult) {
            if (state.value == SCANNING) {
                if (!isGoodScan(scan)) {
                    return
                }
                val mac = scan.device.address
                synchronized(lock) {
                    val device = devices[mac]
                    if (device != null) {
                        device.update(scan)
                    } else {
                        devices[mac] = ScannedDevice(mac, scan.device).update(scan)
                    }
                }
                // also clear BT error if any
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            // did not ask for it, don't care
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e(TAG, "BT Error $errorCode")
            // worst case scenario I seen required asking user to restart BT / reboot device
            // send state.postValue(BT_EXPLODED) along with code...
        }
    }

    init {
        data.addSource(navigation.screen) { evalState() }
        data.addSource(appState.permissions) { evalState() }
        data.addSource(appState.location) { evalState() }
        data.addSource(appState.bluetooth) { evalState() }
        data.addSource(appState.foreground) { evalState() }
    }

    override fun getState(): LiveData<Scanner.State> {
        return state
    }

    override fun getResults(): LiveData<List<ScannedDevice>> {
        return data
    }

    private fun dispatch() {
        synchronized(lock) {
            val now = System.currentTimeMillis()
            devices.entries.removeAll { now - it.value.lastSeen > MAX_AGE }
            val sorted = devices.values.sortedByDescending { it.averageRssi }
            data.postValue(sorted)
        }
        handler.postDelayed(disptach, DISPATCH)
    }

    private fun evalState() {
        val why = when {
            appState.permissions.value?.peek() != true -> NO_PERMISSIONS
            appState.location.value != true -> NO_LOCATION
            appState.bluetooth.value != true -> NO_BLUETOOTH
            appState.foreground.value != true -> NO_FOREGROUND
            navigation.screen.value?.peek() != Navigation.Screen.SCAN -> NO_OBSERVERS
            else -> SCANNING
        }
        val wasScanning = state.value == SCANNING
        val canScanNow = why == SCANNING
        state.value = why
        if (wasScanning xor canScanNow) {
            if (canScanNow) {
                start()
            } else {
                stop(appState.bluetooth.value == true)
            }
        }
    }

    private fun start() {
        synchronized(lock) {
            Log.d(TAG, "Scanner started")
            handler.postDelayed(disptach, DISPATCH)
            // in production I'd wrap this in try/catch logic, as I've seen some weird stuff
            scanner.startScan(null, SETTINGS, callback)
        }
    }

    private fun stop(forReal: Boolean) {
        synchronized(lock) {
            if (forReal) {
                // in production I'd wrap this in try/catch logic, as I've seen some weird stuff
                scanner.stopScan(callback)
            }
            handler.removeCallbacksAndMessages(null)
            Log.d(TAG, "Scanner stopped")
        }
    }

    companion object {

        private val DISPATCH = 1000L
        private val MAX_AGE = 30000L
        private val SETTINGS = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        // Because as an Android dev I've seen things you people wouldn't believe
        // Bluetooth APIs on fire out of cheap Huawei factories
        // I watched BT-Adapters sparking with 133 status codes in the wild
        // Out of spec Chinese devices with rssi power greater than thousand Suns
        // All those moments will be lost in time, like Kitkat support on Play Store soon (hopefully)
        private fun isGoodScan(scanResult: ScanResult): Boolean {
            val device = scanResult.device ?: return false
            if (device.address.isNullOrBlank()) {
                return false
            }
            if (scanResult.rssi >= 0) {
                return false
            }
            if (scanResult.scanRecord == null) {
                return false
            }
            return true
        }
    }
}