package com.example.konsystsecureapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.konsystsecureapp.EventActivity
import com.example.konsystsecureapp.Preferences.PreferenceManager
import com.example.konsystsecureapp.R
import com.example.konsystsecureapp.data.Event
import com.example.konsystsecureapp.data.EventStatus
import com.example.konsystsecureapp.databinding.ItemEventBinding

class EventAdapter(private var events: List<Event>, private val context: Context, private val onEventClickListener: (Int) -> Unit) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount() = events.size
    fun updateData(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }
    inner class EventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(event: Event) {
            val tvStatus = binding.tvStatus
            when(event.status){
                EventStatus.EXPIRED -> {
                    tvStatus.text = "СРОК ИСТЕКАЕТ"
                    tvStatus.visibility = View.VISIBLE
                    tvStatus.setBackgroundResource(R.drawable.rounded_expired_background)
                }
                EventStatus.ONGOING -> {
                    tvStatus.text = ""
                    tvStatus.setBackgroundResource(android.R.color.transparent)
                }
                EventStatus.REVIEW -> {
                    tvStatus.text = "НА ПРОВЕРКЕ"
                    tvStatus.visibility = View.VISIBLE
                    tvStatus.setBackgroundResource(R.drawable.rounded_review_background)
                }
                else -> {
                    tvStatus.text = ""
                    tvStatus.visibility = View.GONE
                    tvStatus.setBackgroundResource(android.R.color.transparent)
                }
            }
            binding.tvTitle.text = event.title
            binding.tvDate.text = event.date
            binding.tvScenariosCount.text = event.scenariosComplete.toString()+"/"+event.scenariosCount.toString()
            val bundle = Bundle().apply {
                putString("eventTitle", event.title)
            }
            binding.root.setOnClickListener {
                PreferenceManager.init(context)
                val eventId = events[adapterPosition].id
                val eventTitle = events[adapterPosition].title
                onEventClickListener(eventId)
                PreferenceManager.saveEventId(eventId)
                PreferenceManager.saveEventTitle(eventTitle)
                val intent = Intent(it.context, EventActivity::class.java)
                intent.putExtras(bundle)
                it.context.startActivity(intent)
            }
        }
    }
}

