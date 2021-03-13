package com.onreg01.locationtracker

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationTrackerService : Service() {

    private var locationClient: FusedLocationProviderClient? = null

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented")
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = LocationServices.getFusedLocationProviderClient(this)

    }
}