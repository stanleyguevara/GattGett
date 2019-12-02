package com.stanleyguevara.gattgett.screens.scan

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.stanleyguevara.gattgett.data.AppState
import com.stanleyguevara.gattgett.data.model.ScannedDevice
import com.stanleyguevara.gattgett.scanner.Scanner
import com.stanleyguevara.gattgett.screens.login.GoogleLoginHelper
import org.koin.core.KoinComponent
import org.koin.core.inject

class ScanViewModel : ViewModel(), KoinComponent {

    private val scanner: Scanner by inject()
    private val appState: AppState by inject()
    private val loginHelper: GoogleLoginHelper by inject()
    val account = Transformations.map(loginHelper.account) {
        it.peek()
    }

    fun getBleDevices(): LiveData<List<ScannedDevice>> {
        return scanner.getResults()
    }

    fun isScanning(): LiveData<Scanner.State> {
        return scanner.getState()
    }

    fun connect(device: BluetoothDevice) {
        appState.setSelectedDevice(device)
    }

}