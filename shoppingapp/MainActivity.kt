package com.example.shoppingapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get the ProgressBar
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        // Set it to indeterminate (infinite animation)
        progressBar.isIndeterminate = true
        progressBar.visibility = ProgressBar.VISIBLE

        // Delay for 3 seconds and then open UserLogin activity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@MainActivity, UserLogin::class.java)
            startActivity(intent)
            finish() // Optional: finish MainActivity so user can't go back
        }, 3000) // 3000 milliseconds = 3 seconds
    }
}
