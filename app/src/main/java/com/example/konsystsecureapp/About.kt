package com.example.konsystsecureapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.konsystsecureapp.Preferences.PreferenceManager
import com.example.konsystsecureapp.databinding.ActivityAboutBinding
import com.example.konsystsecureapp.databinding.ActivityUserProfileBinding
import com.example.konsystsecureapp.network.NetworkService

class About : AppCompatActivity() {
    private var networkService = NetworkService()
    lateinit var binding : ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        PreferenceManager.init(this)
        /*-- networkService.isTokenValid { isValid, message ->
            if (isValid) {
            } else {
                val intent = Intent(this@About, Auth::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }} --*/
        binding.apply{
            setSupportActionBar(aboutToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.chevron_left)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            /*--
            networkService.isTokenValid { isValid, message ->
                if (isValid) {
                    finish()
                } else {
                    val intent = Intent(this@About, Auth::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                --*/
            finish()
            }
        return true
        }
    }
