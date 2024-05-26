package com.example.konsystsecureapp.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.konsystsecureapp.Preferences.PreferenceManager
import com.example.konsystsecureapp.ScenarioActivity
import com.example.konsystsecureapp.data.Scenario
import com.example.konsystsecureapp.databinding.ItemScenarioBinding

class ScenarioAdapter(private val scenarios: List<Scenario>, private val context: Context, private val onScenarioClickListener: (Int) -> Unit) : RecyclerView.Adapter<ScenarioAdapter.ScenarioViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScenarioViewHolder {
        val binding = ItemScenarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScenarioViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ScenarioViewHolder, position: Int) {
        holder.bind(scenarios[position])
    }
    override fun getItemCount() = scenarios.size
    inner class ScenarioViewHolder(private val binding: ItemScenarioBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(scenario: Scenario) {
            binding.tvTitle.text = scenario.title
            binding.tvDate.text = scenario.date
            val bundle = Bundle().apply {
                putString("scenarioTitle", scenario.title)
                putString("scenarioDesc", scenario.description)
                putString("scenarioDate", scenario.date)
                putString("scenarioLocation", scenario.location)
            }
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