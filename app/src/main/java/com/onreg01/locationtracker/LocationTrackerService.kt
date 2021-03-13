package com.onreg01.locationtracker

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.onreg01.locationtracker.db.DatabaseProvider
import com.onreg01.locationtracker.db.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

const val CHANNEL_ID = "111"
const val NOTIFICATION_ID = 777

class LocationTrackerService : Service() {

    companion object {
        val locationRequest: LocationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
    }

    private var locationClient: FusedLocationProviderClient? = null

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            log("locationCallback lastLocation: ${p0.lastLocation}")
        }

        override fun onLocationAvailability(p0: LocationAvailability) {
            super.onLocationAvailability(p0)
            log("locationCallback isLocationAvailable: ${p0.isLocationAvailable}")
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented")
    }

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        lunchForeground()
        log("start service")
        locationClient = LocationServices.getFusedLocationProviderClient(this)
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val locationAvailability = locationClient?.locationAvailability?.await()
                log("locationAvailability: ${locationAvailability?.isLocationAvailable}")
            } catch (e: Exception) {
                log("locationAvailability error: $e")
            }

            try {
                val lastLocation = locationClient?.lastLocation?.await()
                log("lastLocation: $lastLocation")
            } catch (e: Exception) {
                log("lastLocation error $e")
            }

            locationClient?.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())?.addOnCompleteListener {
                if (it.isSuccessful) {
                    log("requestLocationUpdates success")
                } else {
                    log("requestLocationUpdates error: ${it.exception}")
                }
            }

        }
    }

    private fun lunchForeground() {
        val notificationManager = getSystemService(NotificationManager::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Default channel", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }

        startForeground(
            NOTIFICATION_ID,
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location tracker service")
                .setContentText("Obtaining location")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        locationClient?.removeLocationUpdates(locationCallback)
        locationClient = null
    }
}