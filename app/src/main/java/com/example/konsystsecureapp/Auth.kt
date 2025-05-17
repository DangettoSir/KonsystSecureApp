package com.example.konsystsecureapp


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.konsystsecureapp.Preferences.PreferenceManager
import com.example.konsystsecureapp.databinding.ActivityAuthBinding
import com.example.konsystsecureapp.network.AuthResponse
import com.example.konsystsecureapp.network.NetworkService
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject


class Auth : AppCompatActivity() {
    private var networkService = NetworkService()
    lateinit var binding : ActivityAuthBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferenceManager.init(this)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressBar = binding.progressBar
        networkService.isTokenValid { isValid, message ->
            showProgressBar()
            if (isValid) {
                hideProgressBar()
                val intent = Intent(this@Auth, EventActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                hideProgressBar()

            }
        }
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                moveTaskToBack(true)
            }
        }
        binding.loginButton.setOnClickListener {
            val loginButton = binding.loginButton
            buttonToggle(loginButton)
            val login = binding.loginEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            showProgressBar()
            networkService.authenticate(login, password) { success, message ->
                hideProgressBar()
                if (success) {
                    buttonToggle(loginButton)
                    onAuthSuccess(message ?: "")
                    showSuccessToast("Вы успешно авторизовались")
                    val intent = Intent(this@Auth, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                } else {
                    buttonToggle(loginButton)
                    showErrorToast(message ?: "Unknown error")
                }
            }
        }
    }




    fun buttonToggle(button: Button) {
        runOnUiThread {
            button.isEnabled = !button.isEnabled
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


    private fun onAuthSuccess(message: String) {
        val gson = Gson()
        val authResponse = gson.fromJson(message, AuthResponse::class.java)
        PreferenceManager.saveAuthToken(authResponse.token)
        PreferenceManager.saveProtectedAuthToken(authResponse.protectedToken)
        PreferenceManager.saveUsername(authResponse.username)
        PreferenceManager.saveUserId(authResponse.userId)
        PreferenceManager.saveUserNickname(authResponse.userNickname)
    }

    private fun showSuccessToast(message: String) {
        runOnUiThread {
            Toast.makeText(this@Auth, message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun showErrorToast(message: String) {
        runOnUiThread {
            Toast.makeText(this@Auth, message, Toast.LENGTH_SHORT).show()
        }
    }}
