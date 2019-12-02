package com.stanleyguevara.gattgett.screens.permission

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.sensorberg.permissionbitte.PermissionBitte
import com.sensorberg.permissionbitte.Permissions
import com.stanleyguevara.gattgett.R
import com.stanleyguevara.gattgett.screens.BaseFragment
import com.stanleyguevara.gattgett.screens.permission.PermissionViewModel.State.*
import kotlinx.android.synthetic.main.fragment_permission.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PermissionFragment : BaseFragment(), Observer<PermissionViewModel.State>, View.OnClickListener {

    private val TAG: String = PermissionFragment::class.java.simpleName

    private val vm: PermissionViewModel by viewModel()
    private var alertDialog: AlertDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.getState().observe(viewLifecycleOwner, this)
        PermissionBitte.permissions(activity).observe(viewLifecycleOwner,
            Observer<Permissions> { permissions ->
                for (permission in permissions.permissionSet) {
                    Log.d(TAG, "${permission.name} ${permission.result}")
                }
                vm.onPermissionChanged(permissions)
            })
        button.setOnClickListener(this)
    }

    override fun getTitle(): String {
        return "Would you be so kind to..."
    }

    override fun onClick(v: View?) {
        vm.askForPermission()
    }

    override fun onChanged(state: PermissionViewModel.State?) {
        when (state) {
            NEED_ASKING_FOR_PERMISSION -> button.visibility = View.VISIBLE
            SHOW_RATIONALE -> showRationaleDialog()
            ASK_FOR_PERMISSION -> PermissionBitte.ask(activity)
            PERMISSION_GRANTED -> {
                // Handled in ViewModel
            }
            SHOW_SETTINGS -> {
                Toast.makeText(activity, "We really need those permissions", Toast.LENGTH_SHORT).show()
                PermissionBitte.goToSettings(activity)
            }
        }
    }

    private fun showRationaleDialog() {
        alertDialog?.dismiss()
        alertDialog = AlertDialog.Builder(activity!!)
            .setTitle("Asking nicely")
            .setMessage("No permissions, no worky worky")
            .setPositiveButton(
                "OK"
            ) { _, _ -> vm.askForPermission() }
            .setNegativeButton(
                "Nein"
            ) { _, _ -> vm.rationaleDeclined() }
            .setCancelable(false)
            .show()
    }
}