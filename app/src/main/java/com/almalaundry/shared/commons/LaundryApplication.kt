package com.almalaundry.shared.commons

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class LaundryApplication : Application() {
    // yang di androidmanifest

    override fun onCreate() {
        super.onCreate()
    }
}