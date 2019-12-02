package com.stanleyguevara.gattgett.utils

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.lifecycle.LiveData
import org.koin.core.KoinComponent
import org.koin.core.inject

class BluetoothEnabled : LiveData<Boolean>(), KoinComponent {

    private val TAG: String = BluetoothEnabled::class.java.simpleName

    private val context: Context by inject()

    init {
        updateState()
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateState()
        }
    }

    private fun isBluetoothOn(): Boolean {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        return if (adapter != null) {
            adapter.isEnabled && adapter.bluetoothLeScanner != null
        } else {
            false
        }
    }

    private fun updateState() {
        val enabled = isBluetoothOn()
        if (value == enabled) {
            return
        }
        if (enabled) {
            Log.d(TAG, "Bluetooth enabled")
        } else {
            Log.d(TAG, "Bluetooth disabled")
        }
        value = enabled
    }

    override fun onActive() {
        context.registerReceiver(receiver, FILTER)
    }

    override fun onInactive() {
        context.unregisterReceiver(receiver)
    }

    companion object {
        private val FILTER = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
    }

}