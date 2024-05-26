package com.example.konsystsecureapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.example.konsystsecureapp.Preferences.PreferenceManager
import com.example.konsystsecureapp.databinding.ActivitySettingsBinding
import com.example.konsystsecureapp.network.NetworkService

class Settings : AppCompatActivity() {
    private var networkService = NetworkService()
    lateinit var binding : ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val bundle = intent.extras
        setContentView(binding.root)
        PreferenceManager.init(this)
        networkService.isTokenValid { isValid, message ->
            if (isValid) {
            } else {
                val intent = Intent(this@Settings, Auth::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding.apply{
            setSupportActionBar(settingsToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.chevron_left)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            profileButton.setOnClickListener {
            }
            notifyButton.setOnClickListener {
                Toast.makeText(this@Settings, "Уведомления", Toast.LENGTH_SHORT).show()
            }
            darkButton.setOnClickListener {
                Toast.makeText(this@Settings, "Темная тема", Toast.LENGTH_SHORT).show()
            }
            notifySwitch.setOnCheckedChangeListener{_,isChecked ->
                if(isChecked==true){
                    Toast.makeText(this@Settings,"Уведомления включены", Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(this@Settings,"Уведомления выключены", Toast.LENGTH_SHORT).show()
                }
            darkSwitch.setOnCheckedChangeListener{_,isChecked->
                if(isChecked==true){
                    Toast.makeText(this@Settings,"Темная тема включена", Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(this@Settings,"Темная тема выключена", Toast.LENGTH_SHORT).show()
                }
            }

            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            networkService.isTokenValid { isValid, message ->
                if (isValid) {
                    finish()
                } else {
                    val intent = Intent(this@Settings, Auth::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        }
        return true
    }
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
//        menuInflater.inflate(R.menu.settings_menu, menu)
//        return true
//    }
}   