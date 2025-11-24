package com.chatguru.ai

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ChatGuruApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // App initialization
    }
}
