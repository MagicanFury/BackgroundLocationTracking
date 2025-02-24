package com.ztechno.applogclient

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.ztechno.applogclient.services.LocationService
import com.ztechno.applogclient.ui.theme.AppLogClientTheme
import com.ztechno.applogclient.utils.ZDevice
import com.ztechno.applogclient.utils.ZLog
import com.ztechno.applogclient.utils.hasActivityRecognitionPermission
import com.ztechno.applogclient.utils.hasLocationPermission

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity(var viewModel: MainViewModel = MainViewModel()) : ComponentActivity() {
    
    private lateinit var locationService: LocationService
    private var bound: Boolean = false
    
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            val binder = service as LocationService.LocalBinder
            locationService = binder.getService()
            bound = true
            viewModel.loadValue(locationService)
            
            ZLog.error("LocationService BOUND!")
        }
        override fun onServiceDisconnected(arg0: ComponentName) {
            bound = false
            ZLog.error("LocationService UNBOUND!!")
        }
    }
    
    override fun onResume() {
        super.onResume()
        if (!bound) {
            // Bind to LocationService
            Intent(this, LocationService::class.java).also { intent ->
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!this.hasLocationPermission()) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }
        if (!this.hasActivityRecognitionPermission()) {
            ZLog.error("[MainActivity] No Activity Recognition Permission!")
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 0);
        } else {
            ZLog.write("[MainActivity] Has Activity Recognition Permission")
        }
        val btnSize = Modifier.size(width = 360.dp, height = 40.dp)
        setContent {
            AppLogClientTheme {
                val ctx = LocalContext.current
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    
                    Text(text = viewModel.mutableValue)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(modifier = btnSize, onClick = {
                        Intent(applicationContext, LocationService::class.java).apply {
                            action = LocationService.ACTION_START
                            startService(this)
                        }
                    }) {
                        Text(text = "Start")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(modifier = btnSize, onClick = {
                        Intent(applicationContext, LocationService::class.java).apply {
                            action = LocationService.ACTION_STOP
                            startService(this)
                        }
                    }) {
                        Text(text = "Stop")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(modifier = btnSize, onClick = {
                        Intent(applicationContext, LocationService::class.java).apply {
                            action = LocationService.ACTION_RESTART
                            startService(this)
                        }
                    }) {
                        Text(text = "Restart")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(modifier = btnSize, onClick = {
                        Intent(applicationContext, LocationService::class.java).apply {
                            action = LocationService.ACTION_MANUAL
                            startService(this)
                        }
                    }) {
                        Text(text = "Ping")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(modifier = btnSize,onClick = {
                        Intent(applicationContext, LocationService::class.java).apply {
                            action = LocationService.ACTION_SET_HOME_WEESP
                            startService(this)
                        }
                    }) {
                        Text(text = "Set Home to Weesp")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(modifier = btnSize,onClick = {
                        Intent(applicationContext, LocationService::class.java).apply {
                            action = LocationService.ACTION_SET_HOME_KTOWN
                            startService(this)
                        }
                    }) {
                        Text(text = "Set Home to KTown")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(modifier = btnSize, onClick = {
                        ZLog.write("Battery Perc: ${ZDevice.calcBatteryPercentage(applicationContext)}")
                    }) {
                        Text(text = "Battery Check")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(modifier = btnSize, onClick = {
                        val p = packageManager
                        p.setComponentEnabledSetting(
                            componentName,
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP
                        )
                    }) {
                        Text("Hide App")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(modifier = btnSize, onClick = {
                        Intent(applicationContext, LocationService::class.java).apply {
                            action = LocationService.ACTION_SETUP_DEVICE
                            startService(this)
                        }
                    }) {
                        Text("Setup Device Id")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(modifier = btnSize, onClick = {
                        val intent2 = Intent(applicationContext, HistoryActivity::class.java)
                        startActivity(intent2)
                    }) {
                        Text("Show History")
                    }
                    Spacer(modifier = Modifier.height(64.dp))
                }
            }
        }
    }
    
    override fun onStop() {
        super.onStop()
        unbindService(connection)
        bound = false
    }
}