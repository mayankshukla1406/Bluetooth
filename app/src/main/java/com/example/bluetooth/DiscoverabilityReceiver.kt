package com.example.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class DiscoverabilityReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (action != null) {
            if(action == BluetoothAdapter.ACTION_SCAN_MODE_CHANGED){
                when(intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,BluetoothAdapter.ERROR)){
                     BluetoothAdapter.SCAN_MODE_CONNECTABLE ->{
                         Log.d("message2","Connectable")
                     }
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE->{
                        Log.d("message2","Connectable Discoverable")
                    }
                    BluetoothAdapter.SCAN_MODE_NONE->{
                        Log.d("message2","Connectable NONE")
                    }
                    BluetoothAdapter.STATE_CONNECTING->{
                        Log.d("message2","Connectable Connecting")
                    }
                    BluetoothAdapter.STATE_CONNECTED->{
                        Log.d("message2","Connectable Connected")
                    }
                }
            }
        }
    }

}