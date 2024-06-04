package com.example.konsystsecureapp




import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.konsystsecureapp.Preferences.PreferenceManager
import com.example.konsystsecureapp.adapters.EventAdapter
import com.example.konsystsecureapp.data.Event
import com.example.konsystsecureapp.databinding.ActivityMainBinding
import com.example.konsystsecureapp.network.NetworkService
import com.google.gson.JsonObject
import java.util.Collections.emptyList

class MainActivity : AppCompatActivity() {
    private var networkService = NetworkService()
    private lateinit var binding: ActivityMainBinding
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private lateinit var eventAdapter: EventAdapter
    private val eventList = mutableListOf<Event>()
    private lateinit var progressBar: ProgressBar
    private var currentStatus: String = STATUS_ALL
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressBar = binding.progressBar
        PreferenceManager.init(this)
        networkService.isTokenValid { isValid, message ->
            showProgressBar()
            if (isValid) {
                hideProgressBar()
            } else {
                hideProgressBar()
                val intent = Intent(this@MainActivity, Auth::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                moveTaskToBack(true)
            }
        }
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        binding.rvEvents.layoutManager = LinearLayoutManager(this)
        val searchQuery = PreferenceManager.getAuthToken()
        if (searchQuery != null) {
            showProgressBar()
            networkService.searchEvents(searchQuery) { success, message ->
                showProgressBar()
                if (success) {
                    hideProgressBar()
                    val events = message?.let { message ->
                        parseEventsFromJson(message)
                    } ?: emptyList()
                    val filteredEvents = filterEventsByStatus(events)
                    runOnUiThread {
                        val noEventsText = binding.eventtextempty
                        eventList.clear()
                        eventList.addAll(filteredEvents)
                        eventAdapter = EventAdapter(eventList, this@MainActivity) { eventId ->
                            PreferenceManager.saveEventId(eventId)
                        }
                        binding.rvEvents.adapter = eventAdapter
                        if (eventList.isEmpty()) {
                            noEventsText.visibility = View.VISIBLE
                            binding.rvEvents.visibility = View.GONE
                        } else {
                            noEventsText.visibility = View.GONE
                            binding.rvEvents.visibility = View.VISIBLE
                        }
                        updateFilterButtonVisibility(events)
                    }
                } else{
                    hideProgressBar()
                    val noEventsText = binding.eventtextempty
                    runOnUiThread {
                            noEventsText.visibility = View.VISIBLE
                            binding.rvEvents.visibility = View.GONE
                    }
                }
            }
                binding.apply {
                    setSupportActionBar(mainToolbar)
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    supportActionBar?.setHomeAsUpIndicator(R.drawable.burger)
                    supportActionBar?.setDisplayShowTitleEnabled(false)
                    imageButton.setOnClickListener {
                        val intent = Intent(this@MainActivity, UserProfile::class.java)
                        startActivity(intent)
                    }
                    username.text = PreferenceManager.getUsername()
                    nickname.text = PreferenceManager.getUserNickname()
                    binding.rvEvents.layoutAnimation = AnimationUtils.loadLayoutAnimation(
                        this@MainActivity, R.anim.events_rv_layout_animation
                    )
                    binding.rvEvents.scheduleLayoutAnimation()
                    val fullName = PreferenceManager.getUsername()
                    val names = fullName?.trim()?.split(" ")
                    if (names!!.size >= 2) {
                        val initials = "${names[0][0]}${names[1][0]}"
                        avatarText.text = initials
                    }
                    navBurger.setNavigationItemSelectedListener {
                        when (it.itemId) {
                            R.id.item1 -> {
                                val intent = Intent(this@MainActivity, Settings::class.java)
                                startActivity(intent)
                            }

                            R.id.item2 -> {
                                val intent = Intent(this@MainActivity, Support::class.java)
                                startActivity(intent)
                            }

                            R.id.item3 -> {
                                val intent = Intent(this@MainActivity, About::class.java)
                                startActivity(intent)
                            }

                            R.id.item5 -> {
                                val intent = Intent(this@MainActivity, MapTest::class.java)
                                startActivity(intent)
                            }
                        }
                        drawer.closeDrawer(GravityCompat.START)
                        true
                    }
                    setupFilterButtons()
                }

            }
        }

    private fun filterEventsByStatus(events: List<Event>): List<Event> {
        return when (currentStatus) {
            STATUS_REVIEW -> events.filter { it.status.name == "REVIEW" }
            STATUS_EXPIRED -> events.filter { it.status.name == "EXPIRED" }
            else -> events
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
    private fun setupFilterButtons() {
        binding.allbtn.setOnClickListener {
            currentStatus = STATUS_ALL
            updateEventList()
        }

        binding.recentbtn.setOnClickListener {
            currentStatus = STATUS_REVIEW
            updateEventList()
        }

        binding.expiresbtn.setOnClickListener {
            currentStatus = STATUS_EXPIRED
            updateEventList()
        }
    }

    private fun updateEventList() {
        val filteredEvents = filterEventsByStatus(eventList)
        eventAdapter.updateData(filteredEvents)

        // Установка анимации для ListView
        binding.rvEvents.layoutAnimation = AnimationUtils.loadLayoutAnimation(
            this, R.anim.events_rv_layout_animation
        )
        binding.rvEvents.scheduleLayoutAnimation()
    }
    private fun updateFilterButtonVisibility(events: List<Event>) {
        binding.allbtn.visibility = View.VISIBLE
        binding.recentbtn.visibility = View.VISIBLE
        binding.expiresbtn.visibility = View.VISIBLE

        if (!events.any { it.status.name == "REVIEW" }) {
            binding.recentbtn.visibility = View.GONE
        }
        if (!events.any { it.status.name == "EXPIRED" }) {
            binding.expiresbtn.visibility = View.GONE
        }
    }
    private fun parseEventsFromJson(message: String): List<Event> {
        val gson = Gson()
        val jsonObject = gson.fromJson(message, JsonObject::class.java)
        val eventsJsonArray = jsonObject.getAsJsonArray("events")
        val eventType = object : TypeToken<List<Event>>() {}.type
        return gson.fromJson(eventsJsonArray, eventType)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        binding.apply{
            when(item.itemId){
                android.R.id.home -> {drawer.openDrawer(GravityCompat.START)}
                R.id.search -> {
                    Toast.makeText(this@MainActivity,"search", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity, Search::class.java)
                    startActivity(intent)
                }
                R.id.notice -> {
                    Toast.makeText(this@MainActivity,"notice", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity, UserNotice::class.java)
                    startActivity(intent)
                }
            }
        }
        return true
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    companion object {
        const val STATUS_ALL = "upcoming"
        const val STATUS_REVIEW = "review"
        const val STATUS_EXPIRED = "expired"
    }
}
