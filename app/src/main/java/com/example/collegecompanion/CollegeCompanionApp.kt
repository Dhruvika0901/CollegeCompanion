package com.example.collegecompanion

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CollegeCompanionApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val options = FirebaseOptions.Builder()
            .setProjectId("campuscompanionmad")
            .setApplicationId("1:770155174987:android:2a992819042a40849701ce")
            .setApiKey("AIzaSyBTqdl5zST5gI3Uynq8Sh7MEthERYFSKwo")
            .setGcmSenderId("770155174987")
            .setStorageBucket("campuscompanionmad.firebasestorage.app")
            .build()

        FirebaseApp.initializeApp(this, options)
    }
}