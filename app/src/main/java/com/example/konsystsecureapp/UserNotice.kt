package com.example.konsystsecureapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.konsystsecureapp.databinding.ActivitySettingsBinding
import com.example.konsystsecureapp.databinding.ActivityUserNoticeBinding

class UserNotice : AppCompatActivity() {
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
        if(item.itemId == android.R.id.home) finish()
        return true
    }
}