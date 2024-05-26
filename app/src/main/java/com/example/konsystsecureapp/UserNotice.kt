package com.example.konsystsecureapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.konsystsecureapp.databinding.ActivitySettingsBinding
import com.example.konsystsecureapp.databinding.ActivityUserNoticeBinding
import com.example.konsystsecureapp.network.NetworkService

class UserNotice : AppCompatActivity() {
    private var networkService = NetworkService()
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var binding : ActivityUserNoticeBinding
        super.onCreate(savedInstanceState)
        binding = ActivityUserNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply{
            setSupportActionBar(noticeToolbar)
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
                    val intent = Intent(this@UserNotice, Auth::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        }
        return true
    }
}