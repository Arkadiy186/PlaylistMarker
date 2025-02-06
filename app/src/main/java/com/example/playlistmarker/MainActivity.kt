package com.example.playlistmarker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupButton(R.id.search, SearchActivity::class.java)
        setupButton(R.id.settings, SettingsActivity::class.java)
        setupButton(R.id.media_library, MediaLibraryActivity::class.java)
    }

    private fun <T> setupButton(buttonId: Int, targetActivity: Class<T>) {
        findViewById<MaterialButton>(buttonId).setOnClickListener {
            startActivity(Intent(this, targetActivity))
        }
    }
}
