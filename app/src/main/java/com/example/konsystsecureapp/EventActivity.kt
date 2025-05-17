package com.example.konsystsecureapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.konsystsecureapp.Preferences.PreferenceManager
import com.example.konsystsecureapp.adapters.ScenarioAdapter
import com.example.konsystsecureapp.data.Scenario
import com.example.konsystsecureapp.databinding.ActivityEventBinding
import com.example.konsystsecureapp.network.NetworkService
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

class EventActivity : AppCompatActivity() {
    private var networkService = NetworkService()
    private lateinit var binding: ActivityEventBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private lateinit var scenarioAdapter: ScenarioAdapter
    private val scenarioList = mutableListOf<Scenario>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle = intent.extras
        val eventTitle = bundle?.getString("eventTitle")
        PreferenceManager.init(this)
        progressBar = binding.progressBar
        showProgressBar()
        /*-- networkService.isTokenValid { isValid, message ->
            showProgressBar()
            if (isValid) {
                hideProgressBar()
            } else {
                hideProgressBar()
                val intent = Intent(this@EventActivity, Auth::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        } --*/
        binding.eventComplete.isEnabled = false
        val searchQueryEvent = PreferenceManager.getEventId()
        Log.d("searchQueryEvent", searchQueryEvent.toString())
        if (searchQueryEvent != null) {
            networkService.checkEventStatus(searchQueryEvent) { status, message ->
                showProgressBar()
                Log.d("STATUS!!!!", status.toString())
                if(status){
                    hideProgressBar()
                    runOnUiThread {
                        binding.eventComplete.isEnabled = true
                    }

                }
                else{
                    hideProgressBar()
                    runOnUiThread {
                        binding.eventComplete.isEnabled = false
                    }
                }
            }
        }

        binding.rvScenarios.layoutManager = LinearLayoutManager(this)
        val searchQuery = PreferenceManager.getEventId()

        if (searchQuery != null) {
            showProgressBar()
            networkService.searchScenarios(searchQuery) { success, message ->
                if (success) {

                    val scenarios = message?.let { message ->
                        parseScenariosFromJson(message)
                    } ?: emptyList()
                    runOnUiThread {
                        val noScenariosText = binding.scenariotextempty
                        Log.i("ОПА", scenarios.toString())
                        scenarioList.clear()
                        scenarioList.addAll(scenarios)
                        scenarioAdapter = ScenarioAdapter(scenarioList, this@EventActivity, R.layout.item_scenario) { scenarioId ->
                            PreferenceManager.saveScenarioId(scenarioId)
                        }
                        binding.rvScenarios.adapter = scenarioAdapter
                        hideProgressBar()
                        if (scenarioList.isEmpty()) {
                            noScenariosText.visibility = View.VISIBLE
                            binding.rvScenarios.visibility = View.GONE
                            binding.svScenarios.visibility = View.GONE
                        } else{
                            noScenariosText.visibility = View.GONE
                            binding.svScenarios.visibility = View.VISIBLE
                            binding.rvScenarios.visibility = View.VISIBLE
                        }
                    }
                } else {
                    hideProgressBar()
                    val noScenariosText = binding.scenariotextempty
                    runOnUiThread {
                        noScenariosText.visibility = View.VISIBLE
                        binding.rvScenarios.visibility = View.GONE
                        binding.svScenarios.visibility = View.GONE
                    }
                }
            }
            binding.apply {
                setSupportActionBar(scenarioToolbar)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.chevron_left)
                supportActionBar?.setDisplayShowTitleEnabled(false)
                eventtext.text = eventTitle
                eventComplete.setOnClickListener{
                    showProgressBar()
                    val searchQueryEvent = PreferenceManager.getEventId()?.toInt()
                    if (searchQueryEvent != null) {
                        showProgressBar()
                        networkService.updateEventStatus(searchQueryEvent) {success, message ->
                            showProgressBar()
                            if(success){
                                hideProgressBar()
                                val intent = Intent(this@EventActivity, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                            }
                            else{
                                hideProgressBar()
                                showErrorMessage(message.toString())
                            }
                        }
                    }
                }
            }
        }
    }
    private fun showProgressBar() {
        runOnUiThread {
            binding.progressBarLayout.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun hideProgressBar() {
        runOnUiThread {
            binding.progressBarLayout.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }

    private fun parseScenariosFromJson(message: String): List<Scenario> {
        val gson = Gson()
        val jsonObject = gson.fromJson(message, JsonObject::class.java)
        val eventsJsonArray = jsonObject.getAsJsonArray("scenarios")
        val eventType = object : TypeToken<List<Scenario>>() {}.type
        return gson.fromJson(eventsJsonArray, eventType)
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }
}

