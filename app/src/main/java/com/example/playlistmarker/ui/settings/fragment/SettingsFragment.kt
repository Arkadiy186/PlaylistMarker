package com.example.playlistmarker.ui.settings.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.playlistmarker.R
import com.example.playlistmarker.databinding.FragmentSettingsBinding
import com.example.playlistmarker.ui.settings.utills.sharing.ExternalNavigatorContract
import com.example.playlistmarker.ui.settings.utills.sharing.ExternalNavigatorContractImpl
import com.example.playlistmarker.ui.settings.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var externalNavigatorContract: ExternalNavigatorContract

    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        externalNavigatorContract = ExternalNavigatorContractImpl(requireContext())

        setupListeners()

        observeViewModel()
    }

    private fun setupListeners() {
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
        externalNavigatorContract.shareApp(getString(R.string.recommend_android_developer), getString(
            R.string.share_application))
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
        settingsViewModel.themeState.observe(viewLifecycleOwner) { isDarkTheme ->
            binding.darkTheme.isChecked = isDarkTheme
            if (isDarkTheme) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}