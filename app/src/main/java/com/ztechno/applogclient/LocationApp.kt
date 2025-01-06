package com.ztechno.applogclient

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class LocationApp: Application() {
    init {
        instance = this
    }
    companion object {
        private var instance: LocationApp? = null
        
        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location",
                "Location",
                NotificationManager.IMPORTANCE_MIN
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}