package com.albus.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.albus.bluetooth.ui.theme.BluetoothTheme

class MainActivity : ComponentActivity() {
    private val PERMISSION_CODE =1
    private val bluetoothAdapter: BluetoothAdapter= BluetoothAdapter.getDefaultAdapter()
    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){result ->
        if (result.resultCode == RESULT_OK){
            Log.i("Bluetooth", "Permission Ok")
        }else{
            Log.i("Bluetooth", "Permission NO")
        }

    }
    private fun requestBluetoothPermission(){
        val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        activityResultLauncher.launch(enableBluetoothIntent)
    }
    val pairedDevice: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
    var discoverdDevice: Set<BluetoothDevice> = emptySet()

    private  val receiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                when (intent.action){
                    BluetoothDevice.ACTION_FOUND -> {
                        val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        if (device != null){
                            val updates = discoverdDevice.plus(device)
                        }
                        Log.i("Bluetooth", "onReceive : Divice found")
                    }

                    BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                        Log.i("Bluetooth", "onReceive : Started Discovery")
                    }

                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED ->{
                        Log.i("Bluetooth", "onReceive : Finished Discovery")
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun scan(): Set<BluetoothDevice>{
        if (bluetoothAdapter.isDiscovering){
            bluetoothAdapter.cancelDiscovery()
            bluetoothAdapter.startDiscovery()
        }else{
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            bluetoothAdapter.startDiscovery()
        }
        Handler(Looper.getMainLooper()).postDelayed({
            bluetoothAdapter.cancelDiscovery()
        }, 10000L)
        return discoverdDevice
    }

    @RequiresApi(Build.VERSION_CODES.M)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val foundFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        val statFilter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        val endFilter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(receiver, foundFilter)
        registerReceiver(receiver, statFilter)
        registerReceiver(receiver, endFilter)

        if (!bluetoothAdapter.isEnabled){
            requestBluetoothPermission()
        }

        if (SDK_INT >= Build.VERSION_CODES.O){
            if (ContextCompat.checkSelfPermission(
                baseContext, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), PERMISSION_CODE
                )
            }
        }

        enableEdgeToEdge()
        setContent {
            var device: Set<BluetoothDevice> by remember {
                mutableSetOf(emptySet())
            }
            BluetoothTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                   Affichage(

                    )
                }
            }
        }
    }
}

@Composable
fun Affichage(){

}
@Preview
@Composable
fun BluetoothPreview() {
    BluetoothTheme {
        Affichage()
    }
}