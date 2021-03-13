package com.onreg01.locationtracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.onreg01.locationtracker.LocationTrackerService.Companion.locationRequest
import com.onreg01.locationtracker.databinding.ActivityMainBinding
import com.onreg01.locationtracker.db.DatabaseProvider
import com.onreg01.locationtracker.db.log
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

const val REQUEST_CHECK_SETTINGS = 111

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val binding by viewBinding(ActivityMainBinding::bind, R.id.container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val adapter = LogAdapter()
        binding.content.adapter = adapter

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.start -> {
                    LocationServices.getSettingsClient(this)
                        .checkLocationSettings(
                            LocationSettingsRequest.Builder()
                                .addLocationRequest(locationRequest).build()
                        ).addOnCompleteListener {
                            if (it.isSuccessful) {
                                ContextCompat.startForegroundService(this, Intent(this, LocationTrackerService::class.java))
                            } else {
                                it.exception?.let {
                                    if (it is ResolvableApiException) {
                                        it.startResolutionForResult(
                                            this@MainActivity,
                                            REQUEST_CHECK_SETTINGS
                                        )
                                    } else {
                                        log("setting aren't resolvable ${it}")
                                    }
                                }
                            }
                        }
                }
                R.id.clear -> {
                    stopService(Intent(this, LocationTrackerService::class.java))
                    lifecycleScope.launch {
                        DatabaseProvider.db.logDao().clearAll()
                    }
                }
            }
            true
        }

        DatabaseProvider.db.logDao()
            .getAllLogs()
            .onEach { adapter.submitList(it) }
            .launchIn(lifecycleScope)
    }
}