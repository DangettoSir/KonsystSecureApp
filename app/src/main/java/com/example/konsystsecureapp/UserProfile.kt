package com.example.konsystsecureapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.example.konsystsecureapp.databinding.ActivityUserProfileBinding
class UserProfile : AppCompatActivity() {
    lateinit var binding : ActivityUserProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply{
            setSupportActionBar(userprofileToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.chevron_left)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            button.setOnClickListener{
                val intent = Intent(this@UserProfile, UserProfileSettings::class.java)
                startActivity(intent)
            }
            button2.setOnClickListener{
                val intent = Intent(this@UserProfile, Settings::class.java)
                startActivity(intent)
            }
            button3.setOnClickListener{
                Toast.makeText(this@UserProfile,"История отчетов", Toast.LENGTH_SHORT).show()
            }
            button4.setOnClickListener{
                val intent = Intent(this@UserProfile, Auth::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) finish()
        return true
    }
}