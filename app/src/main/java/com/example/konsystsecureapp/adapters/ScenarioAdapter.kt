package com.example.konsystsecureapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.konsystsecureapp.Preferences.PreferenceManager
import com.example.konsystsecureapp.R
import com.example.konsystsecureapp.ScenarioActivity
import com.example.konsystsecureapp.Search
import com.example.konsystsecureapp.data.Event
import com.example.konsystsecureapp.data.Scenario
import com.example.konsystsecureapp.databinding.ItemScenarioBinding
import com.example.konsystsecureapp.databinding.ItemScenarioSearchBinding

class ScenarioAdapter(
    private var scenarios: List<Scenario>,
    private val context: Context,
    private val layoutResId: Int,
    private val onScenarioClickListener: (Int) -> Unit
) : RecyclerView.Adapter<ScenarioAdapter.ScenarioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScenarioViewHolder {
        val binding = when (layoutResId) {
            R.layout.item_scenario -> ItemScenarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            R.layout.item_scenario_search -> ItemScenarioSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            else -> throw IllegalArgumentException("Invalid layout resource ID: $layoutResId")
        }
        return ScenarioViewHolder(binding, onScenarioClickListener)
    }

    override fun onBindViewHolder(holder: ScenarioViewHolder, position: Int) {
        val scenario = scenarios[position]
        holder.bind(scenario)

    }

    override fun getItemCount() = scenarios.size

    fun updateData(newScenarios: List<Scenario>) {
        scenarios = newScenarios
        notifyDataSetChanged()
    }

    inner class ScenarioViewHolder(
        private val binding: ViewBinding,
        private val onScenarioClickListener: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(scenario: Scenario) {
            itemView.setOnClickListener {
                if (scenario.isCompleted) {
                    itemView.isClickable = false
                }
            }
            binding.root.setOnClickListener {
                if (scenario.isCompleted) {
                    binding.root.isClickable = false
                }
            }
            when (binding) {
                is ItemScenarioBinding -> {
                    when (scenario.isCompleted) {
                        true -> {
                            binding.root.setOnClickListener {
                                if (scenario.isCompleted) {
                                    binding.root.isClickable = false
                                }
                            }
                            binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.text_transparent))
                            binding.tvDate.setTextColor(ContextCompat.getColor(context, R.color.text_transparent))
                            binding.tvDate.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                ContextCompat.getDrawable(context, R.drawable.clock_small_completed),
                                null,
                                null,
                                null
                            )
                            binding.tvAttachments.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                ContextCompat.getDrawable(context, R.drawable.paperclip_completed),
                                null,
                                null,
                                null
                            )
                            binding.tvComment.setColorFilter(ContextCompat.getColor(context, R.color.main_transparent), PorterDuff.Mode.SRC_IN)
                            binding.tvAttachments.setTextColor(ContextCompat.getColor(context, R.color.text_transparent))
                            binding.btnComplete.setImageResource(R.drawable.rectangle_completed)
                            binding.btnStatus.visibility = View.VISIBLE
                        }
                        false -> {
                            binding.btnStatus.visibility = View.GONE
                        }
                    }
                    binding.tvTitle.text = scenario.title
                    binding.tvDate.text = scenario.date
                    val bundle = createScenarioBundle(scenario)
                    binding.root.setOnClickListener {
                        PreferenceManager.init(context)
                        val scenarioId = scenarios[adapterPosition].id
                        onScenarioClickListener(scenarioId)
                        PreferenceManager.saveScenarioId(scenarioId)
                        val intent = Intent(it.context, ScenarioActivity::class.java)
                        intent.putExtras(bundle)
                        it.context.startActivity(intent)
                    }
                }
                is ItemScenarioSearchBinding -> {
                    when (scenario.isCompleted) {
                        true -> {
                            binding.root.isClickable = false
                            binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.text_transparent))
                            binding.tvDate.setTextColor(ContextCompat.getColor(context, R.color.text_transparent))
                            binding.tvDate.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                ContextCompat.getDrawable(context, R.drawable.clock_small_completed),
                                null,
                                null,
                                null
                            )
                            binding.tvAttachments.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                ContextCompat.getDrawable(context, R.drawable.paperclip_completed),
                                null,
                                null,
                                null
                            )
                            binding.tvComment.setColorFilter(ContextCompat.getColor(context, R.color.main_transparent), PorterDuff.Mode.SRC_IN)
                            binding.tvAttachments.setTextColor(ContextCompat.getColor(context, R.color.text_transparent))
                            binding.btnComplete.setImageResource(R.drawable.rectangle_completed)
                            binding.btnStatus.visibility = View.VISIBLE
                        }
                        false -> {
                            binding.btnStatus.visibility = View.GONE
                        }
                    }
                    binding.tvTitle.text = scenario.title
                    binding.tvDate.text = scenario.date
                    binding.tvFromEvent.text = "Из мероприятия "+"“"+scenario.eventFrom+"“"
                    val bundle = createScenarioBundle(scenario)
                    binding.root.setOnClickListener {
                        PreferenceManager.init(context)
                        val scenarioId = scenarios[adapterPosition].id
                        onScenarioClickListener(scenarioId)
                        PreferenceManager.saveScenarioId(scenarioId)
                        val intent = Intent(it.context, ScenarioActivity::class.java)
                        intent.putExtras(bundle)
                        it.context.startActivity(intent)
                    }
                }
            }
        }

        private fun createScenarioBundle(scenario: Scenario): Bundle {
            return Bundle().apply {
                putString("scenarioTitle", scenario.title)
                putString("scenarioDesc", scenario.description)
                putString("scenarioDate", scenario.date)
                putString("scenarioLocation", scenario.location)
            }
        }
    }
}
