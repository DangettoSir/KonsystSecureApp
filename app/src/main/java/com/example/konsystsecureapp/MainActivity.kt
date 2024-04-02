package com.example.konsystsecureapp
    
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.example.konsystsecureapp.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply{
            setSupportActionBar(mainToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.burger)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            imageButton.setOnClickListener{
                val intent = Intent(this@MainActivity, UserProfile::class.java)
                startActivity(intent)
            }
            navBurger.setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.item1 -> {
                        val intent = Intent(this@MainActivity, Settings::class.java)
                        startActivity(intent)
                    }
                    R.id.item2 -> {
                        val intent = Intent(this@MainActivity, Support::class.java)
                        startActivity(intent)
                    }
                    R.id.item3 -> {
                        val intent = Intent(this@MainActivity, About::class.java)
                        startActivity(intent)
                    }
                }
                drawer.closeDrawer(GravityCompat.START)
                true
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        binding.apply{
            when(item.itemId){
                android.R.id.home -> {drawer.openDrawer(GravityCompat.START)}
                R.id.search -> {
                    Toast.makeText(this@MainActivity,"search", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity, Search::class.java)
                    startActivity(intent)
                }
                R.id.notice -> {
                    Toast.makeText(this@MainActivity,"notice", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity, UserNotice::class.java)
                    startActivity(intent)
                }

            }
        }

        return true
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }




}