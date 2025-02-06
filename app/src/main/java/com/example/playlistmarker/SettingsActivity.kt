package com.example.playlistmarker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmarker.switcher.ThemeSwitcherApp
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {

    private lateinit var themeSwitcher: SwitchMaterial
    private lateinit var backToMainFromSettings: MaterialToolbar
    private lateinit var shareButton: MaterialTextView
    private lateinit var supportImageButton: MaterialTextView
    private lateinit var userAgreementImageButton: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initView()

        setupListeners()

        themeSwitcher.isChecked = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
    }

    private fun initView() {
        themeSwitcher = findViewById(R.id.activity_settings_dark_theme)
        backToMainFromSettings = findViewById(R.id.activity_settings_toolbar)
        shareButton = findViewById(R.id.activity_settings_share_app)
        supportImageButton = findViewById(R.id.activity_settings_message_support)
        userAgreementImageButton = findViewById(R.id.activity_settings_user_agreement)
    }

    private fun setupListeners() {
        backToMainFromSettings.setNavigationOnClickListener {
            finish()
        }

        shareButton.setOnClickListener {
            onShareButtonClicked()
        }

        supportImageButton.setOnClickListener {
            onSupportImageButtonClicked()
        }

        userAgreementImageButton.setOnClickListener {
            onUserAgreementImageButton()
        }

        themeSwitcher.setOnCheckedChangeListener { _, darkTheme ->
            (applicationContext as ThemeSwitcherApp).switchTheme(darkTheme)
        }
    }

    private fun onShareButtonClicked() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.recommend_android_developer))
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_application)))
    }

    private fun onSupportImageButtonClicked() {
        val shareSupportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("danilov-av2004@mail.ru"))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.message_support_text_theme))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.message_support_text_default))
        }
        startActivity(shareSupportIntent)
    }

    private fun onUserAgreementImageButton() {
        val shareUserAgreementIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(getString(R.string.user_agreement_url))
        }
        startActivity(shareUserAgreementIntent)
    }
}