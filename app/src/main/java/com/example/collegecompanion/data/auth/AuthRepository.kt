package com.example.collegecompanion.data.auth

import android.content.Context
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun signInWithGoogle(context: Context): Result<FirebaseUser>
    suspend fun signOut()
}