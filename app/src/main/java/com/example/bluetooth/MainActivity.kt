package com.example.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bluetooth.databinding.ActivityMainBinding
import com.google.android.material.internal.ManufacturerUtils

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    lateinit var bluetoothAdapter : BluetoothAdapter
    lateinit var bluetoothManager: BluetoothManager
    lateinit var receiver : BluetoothReceiver
    lateinit var receiver2 : DiscoverabilityReceiver
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var adapter : DeviceAdapter
    private var deviceList = arrayListOf<DeviceDetail>()
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        linearLayoutManager = LinearLayoutManager(this)
        binding.deviceRecyclerView.adapter = DeviceAdapter(this,deviceList)
        binding.deviceRecyclerView.layoutManager = linearLayoutManager
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        receiver = BluetoothReceiver()
        receiver2 = DiscoverabilityReceiver()
        enableDisableBluetooth()
        binding.btDiscover.setOnClickListener{
            discoverability()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun enableDisableBluetooth(){
        when{
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)==PackageManager.PERMISSION_DENIED->{

            }
            shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_CONNECT)->{
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT),101)
            }
        }
        binding.btONOFF.setOnClickListener{
            if(!bluetoothAdapter.isEnabled){
                bluetoothAdapter.enable()
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivity(intent)

                val intentFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
                registerReceiver(receiver,intentFilter)
            }
            if(bluetoothAdapter.isEnabled){
                bluetoothAdapter.disable()

                val intentFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
                registerReceiver(receiver,intentFilter)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.S)
    private fun discoverability(){
        when{
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE)==PackageManager.PERMISSION_DENIED->{

            }
            shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_ADVERTISE)->{
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT),102)
            }
        }
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,20)
        startActivity(discoverableIntent)

        val intentFilter = IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
        registerReceiver(receiver2,intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        unregisterReceiver(receiver2)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==101){
            if(grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_DENIED){
                Log.d("message1",grantResults[0].toString())
            }
        }
    }
}