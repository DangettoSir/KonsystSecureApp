package com.example.konsystsecureapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.example.konsystsecureapp.Preferences.PreferenceManager
import com.example.konsystsecureapp.databinding.ActivityUserProfileBinding
import com.example.konsystsecureapp.network.NetworkService

class UserProfile : AppCompatActivity() {
    private var networkService = NetworkService()
    lateinit var binding : ActivityUserProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        PreferenceManager.init(this)
        networkService.isTokenValid { isValid, message ->
            if (isValid) {
            } else {
                val intent = Intent(this@UserProfile, Auth::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }}
        binding.apply{
            setSupportActionBar(userprofileToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.chevron_left)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            button.setOnClickListener{
            }
            button2.setOnClickListener{
                val intent = Intent(this@UserProfile, Settings::class.java)
                startActivity(intent)
            }
            button4.setOnClickListener{
                PreferenceManager.clearUserNickname()
                PreferenceManager.clearAuthToken()
                PreferenceManager.clearUsername()
                PreferenceManager.clearProtectedAuthToken()
                val intent = Intent(this@UserProfile, Auth::class.java)
                startActivity(intent)
            }
            binding.profileUsername.text = PreferenceManager.getUsername()
            binding.profileNickname.text = PreferenceManager.getUserNickname()
            val fullName = PreferenceManager.getUsername()
            val names = fullName?.trim()?.split(" ")
            if (names!!.size >= 2) {
                val initials = "${names[0][0]}${names[1][0]}"
                avatarText.text = initials
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            networkService.isTokenValid { isValid, message ->
                if (isValid) {
                    finish()
                } else {
                    val intent = Intent(this@UserProfile, Auth::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        }
        return true
    }
}