package com.example.playlistmarker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        val backToMainButton = findViewById<Button>(R.id.back_to_main)
        backToMainButton.setOnClickListener {
            val backToMainButtonIntent = Intent(this, MainActivity::class.java)
            startActivity(backToMainButtonIntent)
        }
    }
}