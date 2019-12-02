package com.stanleyguevara.gattgett.screens.connect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.stanleyguevara.gattgett.R
import com.stanleyguevara.gattgett.screens.BaseFragment
import kotlinx.android.synthetic.main.fragment_connect.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ConnectFragment : BaseFragment() {

    private val vm: ConnectViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_connect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.status.observe(viewLifecycleOwner, Observer {
            text.text = it
        })
    }

    override fun getTitle(): String {
        return "Device: ${vm.getDevice().name}"
    }
}