package com.stanleyguevara.gattgett.utils

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import org.koin.core.KoinComponent
import org.koin.core.inject

class LocationEnabled : LiveData<Boolean>(), KoinComponent {

    private val TAG: String = LocationEnabled::class.java.simpleName

    private val manager: LocationManager by inject()
    private val context: Context by inject()

    init {
        updateState()
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateState()
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun isLocationOn(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun updateState() {
        val enabled = isLocationOn()
        if (value == enabled) {
            return
        }
        if (enabled) {
            Log.d(TAG, "Location enabled")
        } else {
            Log.d(TAG, "Location disabled")
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
        private val FILTER = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
    }

}