package com.onreg01.locationtracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.onreg01.locationtracker.databinding.ActivityMainBinding
import com.onreg01.locationtracker.db.DatabaseProvider
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val binding by viewBinding(ActivityMainBinding::bind, R.id.container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val adapter = LogAdapter()
        binding.content.adapter = adapter

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.start -> {
                    ContextCompat.startForegroundService(this, Intent(this, LocationTrackerService::class.java))
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