package com.stanleyguevara.gattgett.screens.permission

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sensorberg.permissionbitte.Permissions
import org.koin.core.KoinComponent

class PermissionViewModel : ViewModel(), KoinComponent {

    private val state = MutableLiveData<State>()

    fun getState(): LiveData<State> {
        return state
    }

    fun askForPermission() {
        state.value = State.ASK_FOR_PERMISSION
    }

    fun rationaleDeclined() {
        state.value = State.SHOW_SETTINGS
    }

    fun onPermissionChanged(permissions: Permissions) {
        when {
            permissions.deniedPermanently() -> {
                //val denied: Set<Permission> = permissions.filter(PermissionResult.DENIED)
                state.value = State.SHOW_SETTINGS
            }
            permissions.showRationale() -> {
                //val rationale: Set<Permission> = permissions.filter(PermissionResult.SHOW_RATIONALE)
                state.value = State.SHOW_RATIONALE
            }
            permissions.needAskingForPermission() -> {
                state.value = State.NEED_ASKING_FOR_PERMISSION
            }
            permissions.allGranted() -> {
                state.value = State.PERMISSION_GRANTED
            }
        }
    }

    enum class State {
        PERMISSION_GRANTED,
        ASK_FOR_PERMISSION,
        NEED_ASKING_FOR_PERMISSION,
        SHOW_RATIONALE,
        SHOW_SETTINGS
    }
}