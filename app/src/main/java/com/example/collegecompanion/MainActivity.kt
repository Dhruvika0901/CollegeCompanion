// com/example/collegecompanion/MainActivity.kt
package com.example.collegecompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.collegecompanion.ui.navigation.CollegeCompanionApp
import com.example.collegecompanion.ui.theme.CollegeCompanionTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CollegeCompanionTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CollegeCompanionApp()
                }
            }
        }
    }
}