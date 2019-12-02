package com.stanleyguevara.gattgett.screens

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sensorberg.permissionbitte.Permissions
import com.stanleyguevara.gattgett.data.AppState
import com.stanleyguevara.gattgett.data.Navigation
import com.stanleyguevara.gattgett.data.Repository
import com.stanleyguevara.gattgett.data.model.SyncStuff
import com.stanleyguevara.gattgett.screens.login.GoogleLoginHelper
import com.stanleyguevara.gattgett.utils.zip
import org.koin.core.KoinComponent
import org.koin.core.inject

class MainViewModel : ViewModel(), KoinComponent {

    private val repository: Repository by inject()
    private val loginHelper: GoogleLoginHelper by inject()
    private val navigation: Navigation by inject()
    private val appState: AppState by inject()
    val databaseState: LiveData<Pair<SyncStuff, Int>> =
        zip(repository.getLastEntry(), repository.getEntryCount()) { entry, count ->
            Pair(entry, count)
        }
    val screen = Transformations.map(navigation.screen) {
        it.consume() ?: it.peek()
    }

    fun onPermissionsChanged(permissions: Permissions) {
        appState.setPermissionsEnabled(permissions.allGranted())
    }

    fun getSignInIntent(): Intent {
        return loginHelper.getSignInIntent()
    }

    fun handleSignInIntent(intent: Intent) {
        loginHelper.handleSignInIntent(intent)
    }

    fun requestLogout() {
        loginHelper.logout()
    }

    fun navigateBack() {
        navigation.navigateBack()
    }
}