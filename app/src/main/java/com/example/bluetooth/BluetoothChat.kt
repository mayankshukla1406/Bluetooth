package com.example.bluetooth

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.*
import android.content.pm.PackageManager
import android.os.*
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bluetooth.databinding.ActivityBluetoothChatBinding


class BluetoothChat : AppCompatActivity() {

    lateinit var binding: ActivityBluetoothChatBinding
    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var bluetoothManager: BluetoothManager
    lateinit var btDevicesArray: Array<BluetoothDevice>
    var sendReceive: BluetoothService.SendReceive? = null

    private var REQUEST_BLUETOOTH_CONNECT = 101
    private var REQUEST_ACCESS_COARSE_LOCATION = 102


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBluetoothChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        binding.btBluetoothSwitch.setOnClickListener {
            enableDisableBluetooth()
        }
        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(receiver, filter)
        showPairedDevices()
        binding.btStartScan.setOnClickListener {
            val serverClass = BluetoothService(adapter = bluetoothAdapter, mHandler = mHandler).ServerClass()
            serverClass.start()
        }
        binding.listView.onItemClickListener =
            AdapterView.OnItemClickListener { _: AdapterView<*>, _, i, _ ->
                val clientClass = BluetoothService(adapter = bluetoothAdapter, mHandler = mHandler).ClientClass(
                    btDevicesArray[i])
                binding.txtStatus.text = "Connecting"
                clientClass.start()
            }
        binding.btSendMessage.setOnClickListener {
            val string: String = java.lang.String.valueOf(binding.etSendMessage.text)
            sendReceive?.write(string.toByteArray())
        }

    }
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(message: Message) {
            when (message.what) {
                Contstants.STATE_LISTENING -> binding.txtStatus.text = "Listening"
                Contstants.STATE_CONNECTING -> binding.txtStatus.text = "Connecting"
                Contstants.STATE_CONNECTED -> binding.txtStatus.text = "Connected"
                Contstants.STATE_CONNECTION_FAILED -> binding.txtStatus.text = "Connection Failed"
                Contstants.STATE_MESSAGE_RECEIVED -> {
                    val readBuff = message.obj as ByteArray
                    val tempMsg = String(readBuff, 0, message.arg1)
                    binding.txtReceivedMessage.text = tempMsg
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun showPairedDevices() {
        binding.btShowDevices.setOnClickListener {
            val bt = bluetoothAdapter.bondedDevices
            val strings = arrayOfNulls<String>(bt.size)
            val btDevicesArray = arrayOfNulls<BluetoothDevice>(bt.size)
            var index = 0
            if (bt.size > 0) {
                for (device in bt) {
                    btDevicesArray[index] = device
                    strings[index] = device.name
                    index++
                }
                val arrayAdapter = ArrayAdapter(
                    applicationContext, R.layout.simple_list_item_1, strings)
                binding.listView.adapter = arrayAdapter
            }
        }
    }


    private var receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                    BluetoothAdapter.STATE_OFF -> {
                        Log.d("bluetooth chat app", "Bluetooth off")
                    }
                    BluetoothAdapter.STATE_TURNING_OFF -> {
                        Log.d("bluetooth chat app", "Bluetooth turning off")
                    }
                    BluetoothAdapter.STATE_ON -> {
                        Log.d("bluetooth chat app", "Bluetooth On")
                    }
                    BluetoothAdapter.STATE_TURNING_ON -> {
                        Log.d("bluetooth chat app", "Bluetooth turning on")
                    }
                }
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun enableDisableBluetooth() {
        if(!bluetoothAdapter.isEnabled){
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivity(intent)
        }
        else{
            bluetoothAdapter.disable()
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when (ContextCompat.checkSelfPermission(
                baseContext, Manifest.permission.ACCESS_COARSE_LOCATION
            )) {
                PackageManager.PERMISSION_DENIED -> androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Runtime Permission")
                    .setMessage("Give Permission")
                    .setNeutralButton("Okay", DialogInterface.OnClickListener { dialog, which ->
                        if (ContextCompat.checkSelfPermission(baseContext,
                                Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(this,
                                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                                REQUEST_ACCESS_COARSE_LOCATION)
                        }
                    })
                    .show()
                    .findViewById<TextView>(R.id.message)!!.movementMethod =
                    LinkMovementMethod.getInstance()

                PackageManager.PERMISSION_GRANTED -> {
                    Log.d("bluetooth chat app", "Permission Granted")
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            when (ContextCompat.checkSelfPermission(
                baseContext, Manifest.permission.BLUETOOTH_CONNECT
            )) {
                PackageManager.PERMISSION_DENIED -> androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Runtime Permission")
                    .setMessage("Give Permission")
                    .setNeutralButton("Okay", DialogInterface.OnClickListener { dialog, which ->
                        if (ContextCompat.checkSelfPermission(baseContext,
                                Manifest.permission.BLUETOOTH_CONNECT) !=
                            PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(this,
                                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                                REQUEST_BLUETOOTH_CONNECT)
                        }
                    })
                    .show()
                    .findViewById<TextView>(R.id.message)!!.movementMethod =
                    LinkMovementMethod.getInstance()

                PackageManager.PERMISSION_GRANTED -> {
                    Log.d("bluetooth chat app", "Permission Granted")
                }
            }
        }
    }
}