package com.example.bluetooth

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DeviceAdapter(context: Context,val devicesList: ArrayList<DeviceDetail>):RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceAdapter.DeviceViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val list = devicesList[position]
        holder.deviceAddress.text = list.deviceAddress
        holder.deviceName.text = list.deviceName
    }

    override fun getItemCount(): Int {
        return devicesList.size
    }
    class DeviceViewHolder(view: View):RecyclerView.ViewHolder(view){
        val deviceName : TextView = view.findViewById(R.id.txtDeviceName)
        val deviceAddress : TextView = view.findViewById(R.id.txtDeviceAddress)
    }
}