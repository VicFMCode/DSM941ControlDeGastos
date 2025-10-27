package com.example.dsm941controldegastos.service

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dsm941controldegastos.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await


/**
 * Servicio que maneja toda la lógica de autenticación con Firebase (Email/Password y Google).
 * Utiliza Corrutinas para las operaciones asíncronas y LiveData para la observación del estado.
 */
class AuthService(private val context: Context) { // Clase renombrada de wAuthService a AuthService

    // Inicialización de Firebase Auth
    private val auth: FirebaseAuth = Firebase.auth

    // LiveData para observar el estado del usuario autenticado en tiempo real
    private val _currentUser = MutableLiveData<FirebaseUser?>(auth.currentUser)
    val currentUser: LiveData<FirebaseUser?> = _currentUser

    // Cliente de Google Sign-In
    private val googleSignInClient: GoogleSignInClient

    init {
        // Establece un listener para actualizar el LiveData cada vez que cambia el estado de autenticación
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }

        // Configuración de Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // Solicita el token de ID de Google para autenticarse con Firebase
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    /**
     * Intenta iniciar sesión con correo electrónico y contraseña usando Corrutinas.
     * @return Result<Unit> para manejar éxito o fallo.
     */
    suspend fun signInWithEmail(email: String, password: String): Result<Unit> = try {
        auth.signInWithEmailAndPassword(email, password).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Intenta registrar un nuevo usuario con correo electrónico y contraseña usando Corrutinas.
     * @return Result<Unit> para manejar éxito o fallo.
     */
    suspend fun signUpWithEmail(email: String, password: String): Result<Unit> = try {
        auth.createUserWithEmailAndPassword(email, password).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // --- Funciones de Acceso para Activities (Compatibilidad con código que no usa Corrutinas/ViewModel) ---

    /**
     * Inicia sesión de un usuario con correo electrónico y contraseña (sin Corrutinas).
     * Útil para `LoginActivity` simple.
     * @return Task<AuthResult> para manejo de éxito/fallo tradicional.
     */
    fun signIn(email: String, password: String): com.google.android.gms.tasks.Task<AuthResult> {


        return auth.signInWithEmailAndPassword(email, password)
    }

    /**
     * Obtiene la intención (Intent) para iniciar la actividad de inicio de sesión con Google.
     */
    fun getGoogleSignInIntent() = googleSignInClient.signInIntent

    /**
     * Autentica el usuario en Firebase usando el token de ID de Google obtenido tras el login.
     * @param idToken El token de ID de Google.
     * @return Result<Unit> para manejar éxito o fallo.
     */
    suspend fun signInWithGoogle(idToken: String): Result<Unit> = try {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Cierra la sesión del usuario actual en Firebase y Google.
     */
    fun signOut() {
        auth.signOut()
        googleSignInClient.signOut()
    }

    /**
     * Obtiene el usuario actualmente autenticado de forma sincrónica.
     * @return FirebaseUser? o null si no hay usuario logueado.
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Obtiene el ID (UID) del usuario actualmente autenticado de forma sincrónica.
     * @return String? o null si no hay usuario logueado.
     */
    fun getUserId(): String? {
        return auth.currentUser?.uid
    }
}