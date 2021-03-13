package com.onreg01.locationtracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.onreg01.locationtracker.databinding.ItemLogBinding
import com.onreg01.locationtracker.db.Log
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class LogAdapter : ListAdapter<Log, LogViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        return LogViewHolder(
            ItemLogBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object DiffCallback : DiffUtil.ItemCallback<Log>() {
    override fun areItemsTheSame(oldItem: Log, newItem: Log): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Log, newItem: Log): Boolean {
        return oldItem == newItem
    }
}

class LogViewHolder(val binding: ItemLogBinding) : RecyclerView.ViewHolder(binding.root) {

    private val formatter =
        DateTimeFormatter.ofPattern("dd.MM HH:mm:ss")
            .withLocale(Locale.getDefault())
            .withZone(ZoneId.systemDefault())

    fun bind(log: Log) {
        binding.date.text = formatter.format(log.time)
        binding.text.text = log.text
    }
}