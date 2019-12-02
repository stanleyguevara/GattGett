package com.stanleyguevara.gattgett.screens.scan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.stanleyguevara.gattgett.R
import com.stanleyguevara.gattgett.scanner.Scanner.State.*
import com.stanleyguevara.gattgett.screens.BaseFragment
import kotlinx.android.synthetic.main.fragment_scan.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScanFragment : BaseFragment() {

    private val vm: ScanViewModel by viewModel()

    private val listener = object : OnItemClick {
        override fun onItemClick(selected: ScanHolder) {
            vm.connect(selected.device)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ScanAdapter(listener)
        vm.getBleDevices().observe(viewLifecycleOwner, Observer { adapter.setData(it) })
        vm.isScanning().observe(viewLifecycleOwner, Observer {
            if (it == null) {
                return@Observer
            }
            when (it) {
                SCANNING -> showState(true)
                NO_PERMISSIONS -> showState(true, "Give permissionz plz!")
                NO_LOCATION -> showState(false, "Turn on location services")
                NO_BLUETOOTH -> showState(false, "Turn on Bluetooth")
                NO_FOREGROUND -> showState(false, "App in background")
                NO_OBSERVERS -> showState(false, "Nobody is watching")
                BT_EXPLODED -> showState(false, "Bluetooth error :(\nRestart BT / phone maybe?")
            }
        })
        vm.account.observe(this, Observer {
            it?.let { account ->
                activity?.title = "Let's scan, ${account.givenName}!"
            }
        })
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter
    }

    fun showState(scanning: Boolean, message: String? = null) {
        recycler.isVisible = scanning
        reason.isVisible = !scanning
        reason.text = message
    }

    override fun getTitle(): String {
        return "Let's scan!"
    }
}