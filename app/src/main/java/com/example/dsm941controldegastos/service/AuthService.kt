package com.example.dsm941controldegastos.service

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthService(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val googleSignInClient: GoogleSignInClient

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(com.example.dsm941controldegastos.R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun signIn(email: String, password: String) = auth.signInWithEmailAndPassword(email, password)
    fun signUp(email: String, password: String) = auth.createUserWithEmailAndPassword(email, password)

    fun signOut() {
        auth.signOut()
        googleSignInClient.signOut()
    }

    fun getCurrentUser() = auth.currentUser
    fun getCurrentUserUid(): String? = auth.currentUser?.uid

    fun getGoogleSignInIntent(): Intent = googleSignInClient.signInIntent

    suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
