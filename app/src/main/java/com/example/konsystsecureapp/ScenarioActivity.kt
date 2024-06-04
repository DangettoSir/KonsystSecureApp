package com.example.konsystsecureapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.konsystsecureapp.Preferences.PreferenceManager
import com.example.konsystsecureapp.adapters.StepAdapter
import com.example.konsystsecureapp.data.CreateDataRequest
import com.example.konsystsecureapp.data.Step
import com.example.konsystsecureapp.databinding.ActivityScenarioBinding
import com.example.konsystsecureapp.network.NetworkService
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.mapview.MapView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScenarioActivity : AppCompatActivity(), (Int) -> Unit {
    private var networkService = NetworkService()
    private lateinit var binding: ActivityScenarioBinding
    private val PERMISSION_REQUEST_CODE = 1
    private val REQUEST_CODE_VIDEO = 2
    private lateinit var progressBar: ProgressBar
    private lateinit var stepAdapter: StepAdapter
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private lateinit var mapView: MapView
    private val stepList = mutableListOf<Step>()
    private val NUMBER_OF_PHOTOS = 3
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScenarioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MapKitFactory.initialize(this)
        val bundle = intent.extras
        val eventTitle = bundle?.getString("scenarioTitle")
        val scenarioTitle = bundle?.getString("scenarioTitle")
        val scenarioDesc = bundle?.getString("scenarioDesc")
        val scenarioDate = bundle?.getString("scenarioDate")
        val scenarioLocation = bundle?.getString("scenarioLocation")
        PreferenceManager.init(this)
        progressBar = binding.progressBarSend

        networkService.isTokenValid { isValid, message ->
            if (isValid) {
            } else {
                val intent = Intent(this@ScenarioActivity, Auth::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showProgressBar()
                isEnabled = false
            }
        }
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        mapView = binding.mapview
        requestCameraAndStoragePermissions()
        binding.rvSteps.layoutManager = LinearLayoutManager(this)
        val searchQuery = PreferenceManager.getScenarioId()
        if (searchQuery != null) {
            showProgressBar2()
            networkService.searchSteps(searchQuery) { success, message ->
                showProgressBar2()
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
                            this,
                            ::onActionPhotoClick,
                            ::onActionVideoClick
                        )

                        val noStepsText = binding.steptextempty
                        binding.rvSteps.adapter = stepAdapter
                        hideProgressBar2()
                        if (stepList.isEmpty()) {
                            noStepsText.visibility = View.VISIBLE
                            binding.rvSteps.visibility = View.GONE
                        } else{
                            noStepsText.visibility = View.GONE
                            binding.rvSteps.visibility = View.VISIBLE
                        }
                    }
                } else {
                    hideProgressBar2()
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
                progressBar = binding.progressBarSend
                binding.scenarioComplete.setOnClickListener {
                    showProgressBar2()
                    scenarioComplete.isEnabled = false
                    saveData()
                }
                binding.no.setOnClickListener{
                    binding.progressBarLayout.visibility = View.GONE
                }
                binding.yes.setOnClickListener{
                    finish()
                }
            }
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA)
    }
    private fun onActionPhotoClick(position: Int) {
        openCamera()
    }
    private fun onActionVideoClick(position: Int) {
        openVideoRecorder()
    }
    override fun onDestroy() {
        super.onDestroy()
        finish()
        if (::stepAdapter.isInitialized) {
            clearCache()
        }
    }
    private fun showProgressBar() {
        runOnUiThread {
            binding.progressBarLayout.visibility = View.VISIBLE
        }
    }
    private fun hideProgressBar() {
        runOnUiThread {
            binding.progressBarLayout.visibility = View.GONE
        }
    }
    private fun showProgressBar2() {
        runOnUiThread {
            binding.progressBarSend.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
        }
    }
    private fun hideProgressBar2() {
        runOnUiThread {
            binding.progressBarSend.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }

    private fun clearCache() {
        stepAdapter.steps.forEach { step ->
            step.photoPaths?.let { paths ->
                paths.forEach { path ->
                    File(path).delete()
                }
                step.photoPaths = mutableListOf()
                stepAdapter.notifyItemChanged(stepAdapter.steps.indexOf(step))
            }
            step.videoPath?.let { path ->
                File(path).delete()
                step.videoPath = null
                stepAdapter.notifyItemChanged(stepAdapter.steps.indexOf(step))
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            Log.d("VideoRecording", "Received image data from camera")
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val currentStep = stepAdapter.steps[stepAdapter.clickedPosition]
            if (currentStep.photoPaths == null) {
                currentStep.photoPaths = mutableListOf()
            }
            if (currentStep.photoPaths.size < NUMBER_OF_PHOTOS) {
                val photoFile = saveImageToFile(imageBitmap, currentStep.photoPaths.size)
                currentStep.photoPaths.add(photoFile.absolutePath)
                stepAdapter.notifyItemChanged(stepAdapter.clickedPosition)
            }
        } else if (requestCode == REQUEST_CODE_VIDEO && resultCode == Activity.RESULT_OK) {
            Log.d("VideoRecording", "Received video data from camera")
            val videoUri = data?.data
            videoUri?.let { uri ->
                Log.d("VideoRecording", "Attempting to save video to cache directory")
                val currentStep = stepAdapter.steps[stepAdapter.clickedPosition]
                if (currentStep.videoPath == null) {
                    val videoFile = saveVideoToFile(uri)
                    currentStep.videoPath = videoFile.absolutePath
                    stepAdapter.notifyItemChanged(stepAdapter.clickedPosition)
                } else {
                    Toast.makeText(this, "Видео уже было записано", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Log.e("VideoRecording", "Failed to get video URI from Intent")
                Toast.makeText(this, "Не удалось записать видео", Toast.LENGTH_SHORT).show()
            }
        }
    }





    private fun saveImageToFile(bitmap: Bitmap, index: Int): File {
        val file = File(externalCacheDir, "image_${System.currentTimeMillis()}_$index.jpg")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return file
    }
    private fun getPhotoByteArrays(): List<ByteArray> {
        val photoByteArrays = mutableListOf<ByteArray>()
        val imagesDir = File(externalCacheDir, "images")
        if (imagesDir.exists()) {
            for (file in imagesDir.listFiles() ?: emptyArray()) {
                if (file.name.startsWith("image_")) {
                    val bytes = file.readBytes()
                    photoByteArrays.add(bytes)
                }
            }
        }
        return photoByteArrays
    }


    private fun saveVideoToFile(videoUri: Uri): File {
        val context = applicationContext
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "video_$timeStamp.mp4"
        val storageDir = context.externalCacheDir
        val videoFile = File(storageDir, fileName)

        try {
            val inputStream = contentResolver.openInputStream(videoUri)
            val outputStream = FileOutputStream(videoFile)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            return videoFile
        } catch (e: IOException) {
            Log.e("SaveVideoToFile", "Error saving video file: $e")
            throw e
        }
    }

    private fun getVideoByteArray(): ByteArray? {
        val context = applicationContext
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "video_$timeStamp.mp4"
        val storageDir = context.externalCacheDir
        val videoFile = File(storageDir, fileName)

        return if (videoFile.exists()) {
            try {
                videoFile.readBytes()
            } catch (e: IOException) {
                Log.e("GetVideoByteArray", "Error getting video byte array: $e")
                null
            }
        } else {
            Log.d("GetVideoByteArray", "Video file not found")
            null
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
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            } else {
            }
        }
    }
    private fun openVideoRecorder() {
        val videoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 180) // 3 minutes
        startActivityForResult(videoIntent, REQUEST_CODE_VIDEO)
    }

    fun saveData() {
        val id = PreferenceManager.getScenarioId()!!.toInt()
        val isCompleted = true
        networkService.UpdateScenario(id, isCompleted){ success, message ->
            if(success){
            } else{
                runOnUiThread {
                    Toast.makeText(this, "АШИБКА ПРИ АПТЕЙДЕ Error: $message", Toast.LENGTH_SHORT).show()
                }
            }
        }
        val comment = binding.commentEditText.text.toString()
        val photoByteArrays = getPhotoByteArrays()
        val videoByteArray = getVideoByteArray()
        val createDataRequest = CreateDataRequest(
            userId = PreferenceManager.getUserId(),
            eventId = PreferenceManager.getEventId(),
            scenarioId = PreferenceManager.getScenarioId(),
            stepId = PreferenceManager.getStepId(),
            videoFile = videoByteArray ?: ByteArray(0),
            photoFiles = photoByteArrays,
            userComment = comment ?: "Null"
        )
        networkService.sendDataSteps(createDataRequest){ success, message ->
            showProgressBar2()
            if (success) {
                hideProgressBar2()
                val intent = Intent()
                intent.putExtra("eventTitle", PreferenceManager.getEventTitle())
                setResult(Activity.RESULT_OK, intent)
                finish() // Возвращаемся в предыдущую активность
            } else {
                hideProgressBar2()
                runOnUiThread {
                    Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun sendBundle(): Bundle {
        return Bundle().apply {
            putString("eventTitle", PreferenceManager.getEventTitle())
        }
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

    private fun requestCameraAndStoragePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                REQUEST_CODE_CAMERA
            )
        } else {
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            networkService.isTokenValid { isValid, message ->
                if (isValid) {
                    showProgressBar()
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

    override fun invoke(p1: Int) {
        TODO("Not yet implemented")
    }
}
