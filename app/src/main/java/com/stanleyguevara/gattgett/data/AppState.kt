package com.stanleyguevara.gattgett.data

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stanleyguevara.gattgett.utils.BluetoothEnabled
import com.stanleyguevara.gattgett.utils.ForegroundDetector
import com.stanleyguevara.gattgett.utils.LocationEnabled
import com.stanleyguevara.gattgett.utils.SingleEvent
import org.koin.core.KoinComponent
import org.koin.core.inject

class AppState : KoinComponent {

    private val _permissions = MutableLiveData<SingleEvent<Boolean>>()
    val permissions: LiveData<SingleEvent<Boolean>> = _permissions
    val bluetooth: BluetoothEnabled by inject()
    val location: LocationEnabled by inject()
    val foreground: ForegroundDetector by inject()

    // Most data would be cool to pass as Fragment argument, but not BluetoothDevice, so it ended here
    // (Although one can create and connect BluetoothDevice using mac, but it's hacky reflection...)
    private val _selected = MutableLiveData<BluetoothDevice>()
    val selected: LiveData<BluetoothDevice> = _selected

    fun setPermissionsEnabled(enabled: Boolean) {
        if (_permissions.value?.peek() != enabled) {
            _permissions.value = SingleEvent(enabled)
        }
    }

    fun setSelectedDevice(device: BluetoothDevice?) {
        _selected.value = device
    }
}