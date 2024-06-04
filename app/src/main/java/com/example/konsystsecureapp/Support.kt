package com.example.konsystsecureapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.konsystsecureapp.databinding.ActivitySupportBinding
import com.example.konsystsecureapp.databinding.ActivityUserProfileSettingsBinding
import com.example.konsystsecureapp.network.NetworkService

class Support : AppCompatActivity() {
    private var networkService = NetworkService()
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var binding : ActivitySupportBinding
        super.onCreate(savedInstanceState)
        binding = ActivitySupportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply{
            setSupportActionBar(supportToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.chevron_left)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
        binding.supportPopup.setOnClickListener{
            val popupLayout = binding.popupLayout
            if(popupLayout.visibility == View.GONE){
                showPopupWithAnimation(binding)
            }
        }
        binding.radioButton.setOnClickListener {
                binding.supportPopup.text = binding.radioButton.text
                hidePopupWithAnimation(binding)
                binding.supportButton.isEnabled = true
                binding.radioButton2.isChecked = false
                binding.radioButton3.isChecked = false
                binding.radioButton4.isChecked = false
        }
        binding.radioButton2.setOnClickListener {
                binding.supportPopup.text = binding.radioButton2.text
                hidePopupWithAnimation(binding)
                binding.supportButton.isEnabled = true
                binding.radioButton.isChecked = false
                binding.radioButton3.isChecked = false
                binding.radioButton4.isChecked = false
        }
        binding.radioButton3.setOnClickListener {
                binding.supportPopup.text = binding.radioButton3.text
                hidePopupWithAnimation(binding)
                binding.supportButton.isEnabled = true
                binding.radioButton.isChecked = false
                binding.radioButton2.isChecked = false
                binding.radioButton4.isChecked = false
        }
        binding.radioButton4.setOnClickListener {
                binding.supportPopup.text = binding.radioButton4.text
                hidePopupWithAnimation(binding)
                binding.supportButton.isEnabled = true
                binding.radioButton.isChecked = false
                binding.radioButton2.isChecked = false
                binding.radioButton3.isChecked = false
        }

        binding.supportButton.setOnClickListener {
            val popupLayout = binding.popupLayout
            if(popupLayout.visibility == View.VISIBLE){
                hidePopupWithAnimation(binding)
            }
            binding.supportButton.isEnabled = false
            binding.supportPopup.isEnabled = false
            showProgressBar(binding)
        }
    }
    private fun hidePopupWithAnimation(binding: ActivitySupportBinding) {
        val animation = AnimationUtils.loadAnimation(this, R.anim.slide_down_animation)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                binding.popupLayout.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {
            }
        })
        binding.popupLayout.startAnimation(animation)
    }
    private fun showPopupWithAnimation(binding: ActivitySupportBinding) {
        val layoutAnimation = AnimationUtils.loadLayoutAnimation(this, R.anim.slide_up_popup)
        binding.popupLayout.layoutAnimation = layoutAnimation
        binding.popupLayout.startLayoutAnimation()
        binding.popupLayout.visibility = View.VISIBLE
    }
    private fun showProgressBar(binding: ActivitySupportBinding) {
        binding.progressBarLayout.visibility = View.VISIBLE
        binding.progressBarLayout.requestLayout()
        binding.progressBar.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            binding.progressBarLayout.requestLayout()
            binding.progressBar.visibility = View.GONE
            binding.imageView10.visibility = View.VISIBLE
            binding.tvHeader.visibility = View.VISIBLE
            val tvProgressText = binding.tvProgressBar
            val newText = "Спасибо за фидбек, мы рассмотрим вашу жалобу и постараемся пофиксить данную проблему в ближайшее время"
            updateTextWithAnimation(tvProgressText, newText, binding)
            binding.next.visibility = View.VISIBLE
            binding.next.setOnClickListener {
                val intent = Intent(this@Support, MainActivity::class.java)
                startActivity(intent)
            }
        }, 3000)
    }

    private fun updateTextWithAnimation(textView: TextView, newText: String, binding: ActivitySupportBinding) {
        val animation = AnimationUtils.loadAnimation(this, R.anim.text_change_animation)
        binding.tvHeader.startAnimation(animation)
        binding.next.startAnimation(animation)
        binding.imageView10.startAnimation(animation)
        textView.startAnimation(animation)
        textView.text = newText
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            networkService.isTokenValid { isValid, message ->
                if (isValid) {
                    finish()
                } else {
                    val intent = Intent(this@Support, Auth::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        }
        return true
    }

}