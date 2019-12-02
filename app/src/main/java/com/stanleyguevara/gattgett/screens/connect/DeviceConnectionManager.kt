package com.stanleyguevara.gattgett.screens.connect

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import com.stanleyguevara.gattgett.scanner.ScannerImpl
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.BleManagerCallbacks
import no.nordicsemi.android.ble.callback.profile.ProfileDataCallback
import no.nordicsemi.android.ble.data.Data
import java.util.*

/**
 * Similar to [ScannerImpl] it's quite simplistic approach but should be ok for this task
 * Real world challenges:
 * - Retrying individual characteristic read along with retrying the whole process
 * - Adding delays between operations if device needs it
 * - Writing to the device and awaiting results
 * - And more...
 *
 * Note that it's your responsibility to bond device first if it needs that
 */
class DeviceConnectionManager(context: Context, batteryCallback: BatteryCallback, serialCallback: SerialCallback) :
    BleManager<BleManagerCallbacks>(context) {

    private var serial: BluetoothGattCharacteristic? = null
    private var battery: BluetoothGattCharacteristic? = null
    private var supported: Boolean = false

    private val someGattCallback = object : BleManagerGattCallback() {

        override fun initialize() {
            readCharacteristic(serial).with(serialCallback).enqueue()
            readCharacteristic(battery).with(batteryCallback).enqueue()
        }

        override fun onDeviceDisconnected() {
            serial = null
            battery = null
            supported = false
        }

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            serial = gatt.getService(DEVICE_INFORMATION_SERVICE)?.getCharacteristic(DEVICE_SERIAL_NUMBER)
            battery = gatt.getService(BATTERY_SERVICE)?.getCharacteristic(BATTERY_LEVEL)
            supported = battery != null && serial != null
            return supported
        }

    }

    override fun getGattCallback(): BleManagerGattCallback {
        return someGattCallback
    }

    companion object {
        private val BATTERY_SERVICE = UUID.fromString("0000180F-0000-1000-8000-00805F9B34FB")
        private val BATTERY_LEVEL = UUID.fromString("00002A19-0000-1000-8000-00805F9B34FB")

        private val DEVICE_INFORMATION_SERVICE = UUID.fromString("0000180A-0000-1000-8000-00805F9B34FB")
        private val DEVICE_SERIAL_NUMBER = UUID.fromString("00002A25-0000-1000-8000-00805F9B34FB")
    }
}

interface BatteryLevelCallback {
    fun onBatteryLevel(device: BluetoothDevice, batteryLevel: Int)
}

abstract class BatteryCallback : ProfileDataCallback, BatteryLevelCallback {
    override fun onDataReceived(device: BluetoothDevice, data: Data) {
        if (data.size() != 1) {
            onInvalidDataReceived(device, data)
            return
        }
        val level: Int? = data.getIntValue(Data.FORMAT_UINT8, 0)
        if (level == null || level < BATT_MIN || level > BATT_MAX) {
            onInvalidDataReceived(device, data)
        } else {
            onBatteryLevel(device, level)
        }
    }

    companion object {
        private const val BATT_MIN = 0
        private const val BATT_MAX = 100
    }
}

interface DeviceSerialCallback {
    fun onDeviceSerial(device: BluetoothDevice, deviceSerial: String)
}

abstract class SerialCallback : ProfileDataCallback, DeviceSerialCallback {
    override fun onDataReceived(device: BluetoothDevice, data: Data) {
        if (data.size() == 0) {
            onInvalidDataReceived(device, data)
            return
        }
        val serial: String? = data.getStringValue(0)
        if (serial.isNullOrBlank()) {
            onInvalidDataReceived(device, data)
        } else {
            onDeviceSerial(device, serial)
        }
    }
}