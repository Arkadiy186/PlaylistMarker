package com.example.playlistmarker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backToMainfromSettings = findViewById<MaterialToolbar>(R.id.activity_settings_toolbar)
        backToMainfromSettings.setNavigationOnClickListener {
            finish()
        }

        val shareButton = findViewById<MaterialTextView>(R.id.activity_settings_share_app)
        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.recommend_android_developer))
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_application)))
        }

        val supportImageButton = findViewById<MaterialTextView>(R.id.activity_settings_message_support)
        supportImageButton.setOnClickListener {
            val shareSupportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("danilov-av2004@mail.ru"))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.message_support_text_theme))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.message_support_text_default))
            }
            startActivity(shareSupportIntent)
        }

        val userAgreementImageButton = findViewById<MaterialTextView>(R.id.activity_settings_user_agreement)
        userAgreementImageButton.setOnClickListener {
            val shareUserAgreementIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.user_agreement_url))
            }
            startActivity(shareUserAgreementIntent)
        }
    }
}