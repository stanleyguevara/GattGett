package com.stanleyguevara.gattgett.screens.scan

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stanleyguevara.gattgett.R
import com.stanleyguevara.gattgett.data.model.ScannedDevice
import kotlinx.android.synthetic.main.item_scan.view.*

interface OnItemClick {
    fun onItemClick(selected: ScanHolder)
}

class ScanAdapter(private val listener: OnItemClick) : RecyclerView.Adapter<ScanHolder>() {

    private var data = emptyList<ScannedDevice>()

    fun setData(scannedDevices: List<ScannedDevice>) {
        data = scannedDevices
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_scan, parent, false)
        return ScanHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ScanHolder, position: Int) {
        val device = data[position]
        holder.device = device.source
        holder.itemView.name.text = String.format("%.1f", device.averageRssi)
        holder.itemView.mac.text = device.lastScan?.device?.name ?: device.mac
        holder.itemView.setOnClickListener { listener.onItemClick(holder) }
    }
}

class ScanHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    lateinit var device: BluetoothDevice
}