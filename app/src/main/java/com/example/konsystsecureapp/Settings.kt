package com.example.konsystsecureapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.example.konsystsecureapp.databinding.ActivitySettingsBinding

class Settings : AppCompatActivity() {
    lateinit var binding : ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply{
            setSupportActionBar(settingsToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.chevron_left)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            profileButton.setOnClickListener({
                Toast.makeText(this@Settings,"Профиль", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@Settings, UserProfile::class.java)
                startActivity(intent)
            })
            passwordButton.setOnClickListener({
                Toast.makeText(this@Settings,"Изменить пароль", Toast.LENGTH_SHORT).show()
            })
            notifyButton.setOnClickListener({
                Toast.makeText(this@Settings,"Уведомления", Toast.LENGTH_SHORT).show()
            })
            darkButton.setOnClickListener({
                Toast.makeText(this@Settings,"Темная тема", Toast.LENGTH_SHORT).show()
            })
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
        if(item.itemId == android.R.id.home) finish()
        return true
    }
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
//        menuInflater.inflate(R.menu.settings_menu, menu)
//        return true
//    }
}   