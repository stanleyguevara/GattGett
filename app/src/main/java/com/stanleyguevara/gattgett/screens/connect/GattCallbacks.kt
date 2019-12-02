package com.stanleyguevara.gattgett.screens.connect

import android.bluetooth.BluetoothDevice
import no.nordicsemi.android.ble.BleManagerCallbacks

// Those are not really important for now, as they just log to console
// In case we need error handling it'd become part of flow control
internal class GattCallbacks(private val console: Console) : BleManagerCallbacks {
    override fun onDeviceDisconnecting(device: BluetoothDevice) {
        console.append("Disconnecting...")
    }

    override fun onDeviceDisconnected(device: BluetoothDevice) {
        console.append("Disconnected")
    }

    override fun onDeviceConnected(device: BluetoothDevice) {
        console.append("Connected!")
    }

    override fun onDeviceNotSupported(device: BluetoothDevice) {
        console.append("Device has no serial/battery service")
    }

    override fun onBondingFailed(device: BluetoothDevice) {
        console.append("Bonding failed")
    }

    override fun onServicesDiscovered(device: BluetoothDevice, optionalServicesFound: Boolean) {
        console.append("Services discovered")
    }

    override fun onBondingRequired(device: BluetoothDevice) {
        console.append("Bond the device first, yo!")
    }

    override fun onLinkLossOccurred(device: BluetoothDevice) {
        console.append("Link loss :(")
    }

    override fun onBonded(device: BluetoothDevice) {
        console.append("Bonded!")
    }

    override fun onDeviceReady(device: BluetoothDevice) {
        console.append("Ready")
    }

    override fun onError(device: BluetoothDevice, message: String, code: Int) {
        console.append("Error code $code\n$message")
    }

    override fun onDeviceConnecting(device: BluetoothDevice) {
        console.append("Connecting...")
    }
}