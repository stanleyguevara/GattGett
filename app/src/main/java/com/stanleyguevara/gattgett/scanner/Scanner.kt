package com.stanleyguevara.gattgett.scanner

import androidx.lifecycle.LiveData
import com.stanleyguevara.gattgett.data.model.ScannedDevice

interface Scanner {
    fun getState(): LiveData<State>
    fun getResults(): LiveData<List<ScannedDevice>>

    enum class State {
        SCANNING,
        NO_PERMISSIONS,
        NO_LOCATION,
        NO_BLUETOOTH,
        NO_FOREGROUND,
        NO_OBSERVERS,
        BT_EXPLODED
    }
}