package com.stanleyguevara.gattgett.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stanleyguevara.gattgett.data.Navigation.Screen.*
import com.stanleyguevara.gattgett.screens.login.GoogleLoginHelper
import com.stanleyguevara.gattgett.utils.SingleEvent
import org.koin.core.KoinComponent
import org.koin.core.inject

class Navigation : KoinComponent {

    private val appState: AppState by inject()
    private val loginHelper: GoogleLoginHelper by inject()

    private val _screen = MutableLiveData<SingleEvent<Screen>>()
    val screen: LiveData<SingleEvent<Screen>> = _screen

    init {
        _screen.value = SingleEvent(INIT)
        appState.permissions.observeForever {
            if (it.consume() == false) {
                requestNavTo(PERMISSION)
            } else {
                requestNavTo(SCAN)
            }
        }
        appState.selected.observeForever {
            if (it != null) {
                requestNavTo(CONNECT)
            } else {
                requestNavTo(SCAN)
            }
        }
        loginHelper.account.observeForever {
            if (it.consume() != null) {
                requestNavTo(SCAN)
            } else {
                requestNavTo(LOGIN)
            }
        }
    }

    fun externalLogin() {
        requestNavTo(GOOGLE)
    }

    private fun requestNavTo(request: Screen) {
        when (request) {
            GOOGLE -> navigateTo(GOOGLE)
            PERMISSION -> navigateTo(PERMISSION)
            LOGIN -> navigateTo(ifPossible(LOGIN))
            SCAN -> navigateTo(ifPossible(SCAN))
            CONNECT -> navigateTo(ifPossible(CONNECT))
            INIT -> {
                // No action needed
            }
        }
    }

    private fun ifPossible(request: Screen): Screen {
        return when {
            appState.permissions.value?.peek() != true -> PERMISSION
            loginHelper.account.value?.peek() == null -> LOGIN
            else -> request
        }
    }

    private fun navigateTo(newScreen: Screen) {
        if (_screen.value?.peek() != newScreen) {
            _screen.value = SingleEvent(newScreen)
        }
    }

    fun navigateBack() {
        if (_screen.value?.peek() == CONNECT) {
            requestNavTo(SCAN)
        } else {
            throw IllegalStateException("That shouldn't happen")
        }
    }

    enum class Screen {
        INIT,
        PERMISSION,
        LOGIN,
        GOOGLE,
        SCAN,
        CONNECT,
    }
}