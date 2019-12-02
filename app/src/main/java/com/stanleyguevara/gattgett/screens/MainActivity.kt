package com.stanleyguevara.gattgett.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.sensorberg.permissionbitte.PermissionBitte
import com.stanleyguevara.gattgett.R
import com.stanleyguevara.gattgett.data.Navigation
import com.stanleyguevara.gattgett.data.Navigation.Screen.*
import com.stanleyguevara.gattgett.data.model.SyncStuff
import com.stanleyguevara.gattgett.screens.connect.ConnectFragment
import com.stanleyguevara.gattgett.screens.login.LoginFragment
import com.stanleyguevara.gattgett.screens.permission.PermissionFragment
import com.stanleyguevara.gattgett.screens.scan.ScanFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.KoinComponent

class MainActivity : AppCompatActivity(), KoinComponent, Observer<Navigation.Screen> {

    private val TAG: String = MainActivity::class.java.simpleName

    private val vm: MainViewModel by viewModel()

    private val lastEntryObserver = Observer<Pair<SyncStuff, Int>> {
        it?.let {
            last_entry.text =
                "Device: ${it.first.serial_number} battery ${it.first.battery_level}% | Not synced: ${it.second}"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        vm.screen.observe(this, this)
        vm.databaseState.observe(this, lastEntryObserver)
        PermissionBitte.permissions(this).observe(this,
            Observer {
                vm.onPermissionsChanged(it)
            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_logout -> vm.requestLogout()
            android.R.id.home -> navigateBack()
        }
        return true
    }

    override fun onChanged(screen: Navigation.Screen) {
        Log.d(TAG, "Showing $screen")
        when (screen) {
            PERMISSION -> showFragment(PermissionFragment(), false)
            LOGIN -> showFragment(LoginFragment(), false)
            GOOGLE -> startActivityForResult(vm.getSignInIntent(), LOGIN_REQUEST_CODE)
            SCAN -> showFragment(ScanFragment(), false)
            CONNECT -> showFragment(ConnectFragment(), true)
            else -> {
                // No action needed
            }
        }
    }

    private fun showFragment(fragment: Fragment, showBack: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(showBack)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) {
            return
        }
        when (requestCode) {
            LOGIN_REQUEST_CODE -> vm.handleSignInIntent(data)
            else -> Log.d(TAG, "Unknown intent $requestCode $resultCode $data")
        }
    }

    override fun onBackPressed() {
        navigateBack()
    }

    fun navigateBack() {
        if (vm.screen.value == CONNECT) {
            vm.navigateBack()
        } else {
            super.onBackPressed()
        }
    }

    private fun getCurrentFragment(): BaseFragment? {
        return supportFragmentManager.fragments.find { it.isVisible } as? BaseFragment
    }

    companion object {
        const val LOGIN_REQUEST_CODE = 666
    }
}
