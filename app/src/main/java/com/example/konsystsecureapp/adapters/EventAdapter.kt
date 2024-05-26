package com.example.konsystsecureapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
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

class EventAdapter(private val events: List<Event>, private val context: Context, private val onEventClickListener: (Int) -> Unit) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount() = events.size

    inner class EventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(event: Event) {
            val tvStatus = TextView(context)
            when(event.status){
                EventStatus.EXPIRED -> {
                    tvStatus.text = "СРОК ИСТЕКАЕТ"
                    tvStatus.textSize = 10f
                    tvStatus.setTextColor(Color.WHITE)
                    tvStatus.gravity = Gravity.CENTER
                    tvStatus.setPadding(8,8,8,8)
                    val layoutParams = ViewGroup.MarginLayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    layoutParams.topMargin = 6
                    layoutParams.marginStart = 20
                    tvStatus.layoutParams = layoutParams
                    tvStatus.minHeight = 0
                    tvStatus.setBackgroundResource(R.drawable.rounded_expired_background)
                    tvStatus.typeface = ResourcesCompat.getFont(context, R.font.rubik_regular)
                    val linearLayoutEvent = binding.linearLayoutEvent
                    val tvTitleIndex = linearLayoutEvent.indexOfChild(binding.tvTitle)
                    linearLayoutEvent.addView(tvStatus, tvTitleIndex)
                }
                EventStatus.ONGOING -> {
                    tvStatus.text = ""
                    tvStatus.setBackgroundResource(android.R.color.transparent)
                }
                EventStatus.REVIEW -> {
                    tvStatus.text = "НА ПРОВЕРКЕ"
                    tvStatus.textSize = 10f
                    tvStatus.setTextColor(Color.WHITE)
                    tvStatus.gravity = Gravity.CENTER
                    tvStatus.setPadding(8,8,8,8)
                    val layoutParams = ViewGroup.MarginLayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    layoutParams.topMargin = 6
                    layoutParams.marginStart = 20
                    tvStatus.layoutParams = layoutParams
                    tvStatus.minHeight = 0
                    tvStatus.setBackgroundResource(R.drawable.rounded_review_background)
                    tvStatus.typeface = ResourcesCompat.getFont(context, R.font.rubik_regular)
                    val linearLayoutEvent = binding.linearLayoutEvent
                    val tvTitleIndex = linearLayoutEvent.indexOfChild(binding.tvTitle)
                    linearLayoutEvent.addView(tvStatus, tvTitleIndex)
                }
                else -> {
                    tvStatus.text = ""
                    tvStatus.setBackgroundResource(android.R.color.transparent)
                }
            }
            binding.tvTitle.text = event.title
            binding.tvDate.text = event.date
            binding.tvScenariosCount.text = event.scenariosCount.toString()+"/"+event.scenariosComplete.toString()
            val bundle = Bundle().apply {
                putString("eventTitle", event.title)
            }
            binding.root.setOnClickListener {
                PreferenceManager.init(context)
                val eventId = events[adapterPosition].id
                onEventClickListener(eventId)
                PreferenceManager.saveEventId(eventId)
                val intent = Intent(it.context, EventActivity::class.java)
                intent.putExtras(bundle)
                it.context.startActivity(intent)
            }
        }
    }
}

