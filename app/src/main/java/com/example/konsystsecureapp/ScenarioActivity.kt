package com.example.konsystsecureapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.camera2.CameraManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.konsystsecureapp.Preferences.PreferenceManager
import com.example.konsystsecureapp.adapters.StepAdapter
import com.example.konsystsecureapp.data.Step
import com.example.konsystsecureapp.databinding.ActivityScenarioBinding
import com.example.konsystsecureapp.network.NetworkService
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.mapview.MapView

class ScenarioActivity : AppCompatActivity(), (Int) -> Unit {
    private var networkService = NetworkService()
    private lateinit var binding: ActivityScenarioBinding
    private val PERMISSION_REQUEST_CODE = 1
    private lateinit var stepAdapter: StepAdapter
    private var videoBytes: ByteArray? = null
    private lateinit var mapView: MapView
    private val stepList = mutableListOf<Step>()
    private val photos = mutableListOf<Bitmap?>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScenarioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MapKitFactory.initialize(this)
        val bundle = intent.extras
        val scenarioTitle = bundle?.getString("scenarioTitle")
        val scenarioDesc = bundle?.getString("scenarioDesc")
        val scenarioDate = bundle?.getString("scenarioDate")
        val scenarioLocation = bundle?.getString("scenarioLocation")
        PreferenceManager.init(this)
        networkService.isTokenValid { isValid, message ->
            if (isValid) {
            } else {
                val intent = Intent(this@ScenarioActivity, Auth::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        mapView = binding.mapview
        CameraPermission()
        binding.rvSteps.layoutManager = LinearLayoutManager(this)
        val searchQuery = PreferenceManager.getScenarioId()
        if (searchQuery != null) {
            networkService.searchSteps(searchQuery) { success, message ->
                if (success) {
                    val steps = message?.let{ message ->
                        parseStepsFromJson(message)
                    } ?: emptyList()
                    runOnUiThread {
                        stepList.clear()
                        stepList.addAll(steps)
                        stepAdapter = StepAdapter(
                            stepList,
                            this@ScenarioActivity,
                            photos,
                            this
                        )
                        val noStepsText = binding.steptextempty
                        binding.rvSteps.adapter = stepAdapter
                        if (stepList.isEmpty()) {
                            noStepsText.visibility = View.VISIBLE
                            binding.rvSteps.visibility = View.GONE
                        } else{
                            noStepsText.visibility = View.GONE
                            binding.rvSteps.visibility = View.VISIBLE
                        }
                    }
                } else {
                    val noStepsText = binding.steptextempty
                    runOnUiThread {
                        noStepsText.visibility = View.VISIBLE
                        binding.rvSteps.visibility = View.GONE
                    }
                }
            }
            binding.apply {
                setSupportActionBar(scenariodetailToolbar)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.x_close)
                supportActionBar?.setDisplayShowTitleEnabled(false)
                tvTitle.text = scenarioTitle
                tvDate.text = scenarioDate
                tvDescription.text = scenarioDesc
                tvLocation.text = scenarioLocation
                binding.scenarioComplete.setOnClickListener {
                    saveData()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
        } else {
            MapKitFactory.getInstance().onStart()
            binding.mapview.onStart()
        }
    }
    override fun onStop() {
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                MapKitFactory.getInstance().onStart()
                binding.mapview.onStart()
            } else {
                // Разрешение не предоставлено
            }
        }
    }
    fun onVideoRecorded(videoBytes: ByteArray?) {
        this.videoBytes = videoBytes
    }
    fun saveData (){
        val comment = binding.commentEditText.text.toString()
        val intent = Intent(this@ScenarioActivity, EventActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
    private fun parseStepsFromJson(message: String): List<Step> {
        val gson = Gson()
        val jsonObject = gson.fromJson(message, JsonObject::class.java)
        val eventsJsonArray = jsonObject.getAsJsonArray("steps")
        val eventType = object : TypeToken<List<Step>>() {}.type
        return gson.fromJson(eventsJsonArray, eventType)
    }
    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun CameraPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            networkService.isTokenValid { isValid, message ->
                if (isValid) {
                    finish()
                } else {
                    val intent = Intent(this@ScenarioActivity, Auth::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        }
        return true
    }
    companion object {
        private const val REQUEST_CODE_CAMERA = 123
    }
}
