// com/example/collegecompanion/CollegeCompanionApp.kt
package com.example.collegecompanion

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class CollegeCompanionApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)  // ← clean, no manual options needed
    }
}


