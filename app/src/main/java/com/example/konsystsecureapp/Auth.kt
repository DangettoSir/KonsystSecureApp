package com.example.konsystsecureapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.konsystsecureapp.databinding.ActivityAuthBinding

class Auth : AppCompatActivity() {
    lateinit var binding : ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply{

            loginButton.setOnClickListener{
                val login = loginEditText.text.toString()
                val password = passwordEditText.text.toString()
                if (login.equals("admin", ignoreCase = true) && password.equals("admin", ignoreCase = true)){
                    val intent = Intent(this@Auth, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@Auth, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}