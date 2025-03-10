package com.example.playlistmarker.ui.settings.utills.sharing

import android.content.Context
import android.content.Intent
import android.net.Uri

class ExternalNavigatorContractImpl(private val context: Context) : ExternalNavigatorContract {
    override fun shareApp(text: String, title: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(shareIntent, title))
    }

    override fun sendMessageSupport(mail: String, subject: String, text: String) {
        val shareSupportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(mail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(shareSupportIntent)
    }

    override fun openUserAgreement(url: String) {
        val shareUserAgreementIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        context.startActivity(shareUserAgreementIntent)
    }
}