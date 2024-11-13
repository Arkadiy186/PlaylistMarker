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

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backToMainfromSettings = findViewById<MaterialToolbar>(R.id.activitySettingsToolbar)
        backToMainfromSettings.setNavigationOnClickListener {
            finish()
        }

        val shareButton = findViewById<ImageButton>(R.id.shareButton)
        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.recommend_android_developer))
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_application)))
        }

        val supportImageButton = findViewById<ImageButton>(R.id.supportImageButton)
        supportImageButton.setOnClickListener {
            val shareSupportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("danilov-av2004@mail.ru"))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.message_support_text_theme))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.message_support_text_default))
            }
            if (shareSupportIntent.resolveActivity(packageManager) != null) {
                startActivity(shareSupportIntent)
            } else {
                Toast.makeText(this, getString(R.string.message_support_error_email), Toast.LENGTH_SHORT).show()
            }
        }

        val userAgreementImageButton = findViewById<ImageButton>(R.id.userAgreementImageButton)
        userAgreementImageButton.setOnClickListener {
            val shareUserAgreementIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.user_agreement_url))
            }
            if (shareUserAgreementIntent.resolveActivity(packageManager) != null) {
                startActivity(shareUserAgreementIntent)
            }   else {
                Toast.makeText(this, getString(R.string.user_agreement_error), Toast.LENGTH_SHORT).show()
            }
        }
    }
}