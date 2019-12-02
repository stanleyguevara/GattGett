package com.stanleyguevara.gattgett.screens.login

import androidx.lifecycle.ViewModel
import com.stanleyguevara.gattgett.data.Navigation
import org.koin.core.KoinComponent
import org.koin.core.inject


class LoginViewModel : ViewModel(), KoinComponent {

    val navigation: Navigation by inject()

    fun requestLogin() {
        navigation.externalLogin()
    }
}