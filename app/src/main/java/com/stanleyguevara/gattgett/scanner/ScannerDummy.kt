package com.stanleyguevara.gattgett.scanner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stanleyguevara.gattgett.data.model.ScannedDevice

class ScannerDummy : Scanner {

    private val empty = MutableLiveData<List<ScannedDevice>>()
    private val _scanning = MutableLiveData<Scanner.State>()

    init {
        empty.value = emptyList()
        _scanning.value = Scanner.State.NO_BLUETOOTH
    }

    override fun getState(): LiveData<Scanner.State> {
        return _scanning
    }

    override fun getResults(): LiveData<List<ScannedDevice>> {
        return empty
    }
}