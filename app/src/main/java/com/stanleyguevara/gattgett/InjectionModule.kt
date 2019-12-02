package com.stanleyguevara.gattgett

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.location.LocationManager
import androidx.work.WorkManager
import com.stanleyguevara.gattgett.data.AppState
import com.stanleyguevara.gattgett.data.Navigation
import com.stanleyguevara.gattgett.data.Repository
import com.stanleyguevara.gattgett.data.persist.SyncStuffDatabase
import com.stanleyguevara.gattgett.scanner.ScannerDummy
import com.stanleyguevara.gattgett.scanner.ScannerImpl
import com.stanleyguevara.gattgett.screens.MainViewModel
import com.stanleyguevara.gattgett.screens.connect.ConnectViewModel
import com.stanleyguevara.gattgett.screens.login.GoogleLoginHelper
import com.stanleyguevara.gattgett.screens.login.LoginViewModel
import com.stanleyguevara.gattgett.screens.permission.PermissionViewModel
import com.stanleyguevara.gattgett.screens.scan.ScanViewModel
import com.stanleyguevara.gattgett.utils.BluetoothEnabled
import com.stanleyguevara.gattgett.utils.ForegroundDetector
import com.stanleyguevara.gattgett.utils.LocationEnabled
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

class InjectionModule {

    companion object {
        fun module(): List<Module> {
            return listOf(InjectionModule().module)
        }
    }

    val module = module {
        // system services
        single { androidContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager }
        single {
            val scanner = BluetoothAdapter.getDefaultAdapter()?.bluetoothLeScanner
            if (scanner != null) {
                ScannerImpl(scanner)
            } else {
                // because emulators and some devices have no adapter... :(
                ScannerDummy()
            }
        }
        single { GoogleLoginHelper() }
        single { WorkManager.getInstance(androidContext()) }
        single { BluetoothEnabled() }
        single { LocationEnabled() }
        single { ForegroundDetector() }

        // app data
        single { SyncStuffDatabase.getInstance(androidContext()) }
        single { AppState() }
        single { Repository() }
        single { Navigation() }

        // viewmodels
        viewModel { PermissionViewModel() }
        viewModel { LoginViewModel() }
        viewModel { MainViewModel() }
        viewModel { ScanViewModel() }
        viewModel { ConnectViewModel() }
    }
}
