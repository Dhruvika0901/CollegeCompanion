package com.example.collegecompanion

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// @HiltAndroidApp tells Hilt: "This is the app — set up all dependencies"
// Without this, Hilt won't work anywhere in the app
@HiltAndroidApp
class CollegeCompanionApp : Application()