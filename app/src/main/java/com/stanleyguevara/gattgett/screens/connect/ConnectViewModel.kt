package com.stanleyguevara.gattgett.screens.connect

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stanleyguevara.gattgett.data.AppState
import com.stanleyguevara.gattgett.data.Repository
import com.stanleyguevara.gattgett.data.model.SyncStuff
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.data.Data
import org.koin.core.KoinComponent
import org.koin.core.inject

interface Console {
    fun append(string: String)
}

class ConnectViewModel : ViewModel(), KoinComponent, Console {

    private val context: Context by inject()
    private val appState: AppState by inject()
    private val repository: Repository by inject()

    private val device: BluetoothDevice
    private val _status = MutableLiveData<String>()
    val status: LiveData<String> = _status

    private val builder = SyncStuff.Builder()

    private val serial: SerialCallback = object : SerialCallback() {
        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            append("Invalid serial number data :(\n$data")
        }

        override fun onDeviceSerial(device: BluetoothDevice, deviceSerial: String) {
            setDeviceSerial(deviceSerial)
            append("Device serial is: $deviceSerial")
        }
    }

    private val battery: BatteryCallback = object : BatteryCallback() {
        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            append("Invalid battery data :(\n$data")
        }

        override fun onBatteryLevel(device: BluetoothDevice, batteryLevel: Int) {
            setBatteryLevel(batteryLevel)
            append("Battery level is: $batteryLevel")
        }
    }

    init {
        device = appState.selected.value ?: throw IllegalStateException("Missing selected BluetoothDevice")
        _status.value = "Init"
        connect()
    }

    fun getDevice(): BluetoothDevice {
        return device
    }

    private fun connect() {
        val manager = DeviceConnectionManager(context, battery, serial)
        manager.setGattCallbacks(GattCallbacks(this))
        manager.connect(device)
            .retry(5, 100)
            .useAutoConnect(false)
            .enqueue()
        manager.disconnect().enqueue()
    }

    private fun setDeviceSerial(deviceSerial: String) {
        builder.serial(deviceSerial)
        saveIfReady()
    }

    private fun setBatteryLevel(batteryLevel: Int) {
        builder.battery(batteryLevel)
        saveIfReady()
    }

    private fun saveIfReady() {
        builder.build()?.let {
            viewModelScope.launch {
                repository.saveStuff(it)
            }.invokeOnCompletion {
                append("Written to DB")
            }
        }
    }

    override fun append(string: String) {
        _status.value = "${_status.value}\n$string"
    }

    override fun onCleared() {
        appState.setSelectedDevice(null)
    }
}