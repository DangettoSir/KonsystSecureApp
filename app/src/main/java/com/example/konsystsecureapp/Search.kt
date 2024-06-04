package com.example.konsystsecureapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.konsystsecureapp.Preferences.PreferenceManager
import com.example.konsystsecureapp.adapters.EventAdapter
import com.example.konsystsecureapp.adapters.ScenarioAdapter
import com.example.konsystsecureapp.data.Event
import com.example.konsystsecureapp.data.Scenario
import com.example.konsystsecureapp.databinding.ActivitySearchBinding
import com.example.konsystsecureapp.network.NetworkService
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.util.Collections

class Search : AppCompatActivity() {
    private var networkService = NetworkService()
    private val eventList = mutableListOf<Event>()
    private val scenarioList = mutableListOf<Scenario>()
    private lateinit var eventAdapter: EventAdapter
    private lateinit var scenarioAdapter: ScenarioAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var binding: ActivitySearchBinding
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        PreferenceManager.init(this)
        networkService.isTokenValid { isValid, message ->
            if (isValid) {
            } else {
                val intent = Intent(this@Search, Auth::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }

        binding.rvEvents.layoutManager = LinearLayoutManager(this)
        val searchQuery = PreferenceManager.getAuthToken()
        if (searchQuery != null) {
            networkService.searchEvents(searchQuery) { success, message ->
                if (success) {
                    val events = message?.let { message ->
                        parseEventsFromJson(message)
                    } ?: Collections.emptyList()
                    runOnUiThread {
                        eventList.clear()
                        eventList.addAll(events)
                        eventAdapter = EventAdapter(eventList, this@Search) { eventId ->
                            PreferenceManager.saveEventId(eventId)
                        }
                        binding.rvEvents.adapter = eventAdapter
                    }
                } else {
                    runOnUiThread {
                        binding.rvEvents.visibility = View.GONE
                    }
                }
            }

            binding.rvScenarios.layoutManager = LinearLayoutManager(this)
            val searchQuery = PreferenceManager.getAuthToken()
            if (searchQuery != null) {
                networkService.searchAllScenarios(searchQuery) { success, message ->
                    if (success) {
                        val scenarios = message?.let { message ->
                            parseScenariosFromJson(message)
                        } ?: emptyList()
                        runOnUiThread {
                            scenarioList.clear()
                            scenarioList.addAll(scenarios)
                            scenarioAdapter =
                                ScenarioAdapter(scenarioList, this@Search, R.layout.item_scenario_search) { scenarioId ->
                                    PreferenceManager.saveScenarioId(scenarioId)
                                }
                            binding.rvScenarios.adapter = scenarioAdapter
                            binding.rvScenarios.layoutManager = LinearLayoutManager(this)
                        }
                    } else {
                        runOnUiThread {
                            binding.rvScenarios.visibility = View.GONE
                        }
                    }
                }
            }
                binding.apply {
                    setSupportActionBar(searchToolbar)
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    supportActionBar?.setHomeAsUpIndicator(R.drawable.chevron_left)
                    supportActionBar?.setDisplayShowTitleEnabled(false)
                    searchEditText.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            if (s != null) {
                                val query = s.trim().toString()
                                if (query.isNotEmpty()) {
                                    binding.emptyText.visibility = View.GONE
                                    binding.rvEvents.visibility = View.VISIBLE
                                    binding.rvScenarios.visibility = View.VISIBLE
                                    performSearch(query, binding)
                                    performScenarioSearch(query, binding)
                                } else {
                                    binding.emptyText.visibility = View.VISIBLE
                                    binding.rvEvents.visibility = View.GONE
                                    binding.rvScenarios.visibility = View.GONE
                                    eventAdapter.updateData(eventList)
                                    scenarioAdapter.updateData(scenarioList)

                                }
                            }
                        }

                        override fun afterTextChanged(s: Editable?) {
                            performSearch(s?.trim().toString(), binding)
                        }
                    })
                }
            }
        }

    private fun parseEventsFromJson(message: String): List<Event> {
        val gson = Gson()
        val jsonObject = gson.fromJson(message, JsonObject::class.java)
        val eventsJsonArray = jsonObject.getAsJsonArray("events")
        val eventType = object : TypeToken<List<Event>>() {}.type
        return gson.fromJson(eventsJsonArray, eventType)
    }
    private fun parseScenariosFromJson(message: String): List<Scenario> {
        val gson = Gson()
        val jsonObject = gson.fromJson(message, JsonObject::class.java)
        val eventsJsonArray = jsonObject.getAsJsonArray("scenarios")
        val eventType = object : TypeToken<List<Scenario>>() {}.type
        return gson.fromJson(eventsJsonArray, eventType)
    }
    private fun performSearch(query: String, binding: ActivitySearchBinding) {
        if (query.isEmpty()) {
            eventAdapter.updateData(eventList)
            binding.emptyText.visibility = View.VISIBLE
            binding.eventsText.visibility = View.GONE
            binding.rvEvents.visibility = View.GONE
        } else {
            val filteredEvents = eventList.filter { it.title.contains(query, ignoreCase = true) }
            eventAdapter.updateData(filteredEvents)
            if (filteredEvents.isEmpty()) {
                binding.eventsText.visibility = View.GONE
                binding.rvEvents.visibility = View.GONE
            } else {
                binding.emptyText.visibility = View.GONE
                binding.eventsText.visibility = View.VISIBLE
                binding.rvEvents.visibility = View.VISIBLE
            }
        }
    }
    private fun performScenarioSearch(query: String, binding: ActivitySearchBinding) {
        if (query.isEmpty()) {
            scenarioAdapter.updateData(scenarioList)
            binding.emptyText.visibility = View.VISIBLE
            binding.scenariosText.visibility = View.GONE
            binding.rvScenarios.visibility = View.GONE
        } else {
            val filteredScenarios = scenarioList.filter { it.title.contains(query, ignoreCase = true) }
            scenarioAdapter.updateData(filteredScenarios)
            if (filteredScenarios.isEmpty()) {
                binding.scenariosText.visibility = View.GONE
                binding.rvScenarios.visibility = View.GONE
            } else {
                binding.emptyText.visibility = View.GONE
                binding.scenariosText.visibility = View.VISIBLE
                binding.rvScenarios.visibility = View.VISIBLE
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            networkService.isTokenValid { isValid, message ->
                if (isValid) {
                    finish()
                } else {
                    val intent = Intent(this@Search, Auth::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        }
        return true
    }
}