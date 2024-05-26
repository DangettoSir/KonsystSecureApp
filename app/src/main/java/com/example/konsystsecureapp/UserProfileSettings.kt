package com.example.konsystsecureapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.konsystsecureapp.databinding.ActivityUserNoticeBinding
import com.example.konsystsecureapp.databinding.ActivityUserProfileSettingsBinding
import com.example.konsystsecureapp.network.NetworkService

class UserProfileSettings : AppCompatActivity() {
    private var networkService = NetworkService()
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var binding : ActivityUserProfileSettingsBinding
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle = intent.extras
        if (bundle != null) {
            val login = bundle.getString("login")
            val firstLetter = bundle.getString("firstLetter")
            binding.profileUsername.text = login
            binding.avatarText.text = firstLetter
        }
        binding.apply{
            setSupportActionBar(userprofilesettingsToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.chevron_left)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            networkService.isTokenValid { isValid, message ->
                if (isValid) {
                    finish()
                } else {
                    val intent = Intent(this@UserProfileSettings, Auth::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        }
        return true
    }
}