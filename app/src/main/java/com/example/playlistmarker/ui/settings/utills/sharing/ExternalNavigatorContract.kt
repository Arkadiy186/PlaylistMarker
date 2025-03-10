package com.example.playlistmarker.ui.settings.utills.sharing

interface ExternalNavigatorContract {
    fun shareApp(text: String, title: String)
    fun sendMessageSupport(mail: String, subject: String, text: String)
    fun openUserAgreement(url: String)
}