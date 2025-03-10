package com.example.playlistmarker.ui.settings.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmarker.R
import com.example.playlistmarker.databinding.ActivitySettingsBinding
import com.example.playlistmarker.ui.settings.utills.sharing.ExternalNavigatorContract
import com.example.playlistmarker.ui.settings.utills.sharing.ExternalNavigatorContractImpl
import com.example.playlistmarker.ui.settings.viewmodel.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var externalNavigatorContract: ExternalNavigatorContract


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        externalNavigatorContract = ExternalNavigatorContractImpl(this)

        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        setupListeners()

        observeViewModel()
    }

    private fun setupListeners() {
        binding.settingsToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.shareApp.setOnClickListener {
            onShareButtonClicked()
        }

        binding.messageSupport.setOnClickListener {
            onSupportImageButtonClicked()
        }

        binding.userAgreement.setOnClickListener {
            onUserAgreementImageButton()
        }

        binding.darkTheme.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.onThemeSwitchClicked(isChecked)
        }
    }

    private fun onShareButtonClicked() {
        externalNavigatorContract.shareApp(getString(R.string.recommend_android_developer), getString(R.string.share_application))
    }

    private fun onSupportImageButtonClicked() {
        externalNavigatorContract.sendMessageSupport(
            "danilov-av2004@mail.ru",
            getString(R.string.message_support_text_theme),
            getString(R.string.message_support_text_default)
        )
    }

    private fun onUserAgreementImageButton() {
        externalNavigatorContract.openUserAgreement(getString(R.string.user_agreement_url))
    }

    private fun observeViewModel() {
        settingsViewModel.themeState.observe(this) { isDarkTheme ->
            binding.darkTheme.isChecked = isDarkTheme
            if (isDarkTheme) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}