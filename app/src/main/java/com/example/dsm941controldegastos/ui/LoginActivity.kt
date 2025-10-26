package com.example.dsm941controldegastos.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dsm941controldegastos.service.AuthService // Importación CORRECTA del servicio
import com.example.dsm941controldegastos.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    // Inicializaremos el servicio pasándole el contexto en onCreate
    private lateinit var authService: AuthService

    // View Binding
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar View Binding (asume que activity_login.xml existe)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar el servicio de autenticación
        authService = AuthService(this)

        // 1. Botón de inicio de sesión con Correo/Contraseña
        binding.buttonLogin.setOnClickListener {
            loginUserWithEmail()
        }

        // 2. Navegar a Registro
        binding.textViewRegister.setOnClickListener {
            // Navega a la Activity de Registro
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // 3. Botón de inicio de sesión con Google (asume ID: buttonGoogleLogin)
        binding.buttonGoogleLogin.setOnClickListener {
            signInWithGoogle()
        }
    }

    // Verifica si el usuario ya está logueado al iniciar la Activity
    override fun onStart() {
        super.onStart()
        // Usamos la función síncrona de AuthService
        val currentUser = authService.getCurrentUser()

        if (currentUser != null) {
            navigateToMain()
        }
    }

    // --- Lógica de Correo y Contraseña ---

    private fun loginUserWithEmail() {
        // Asegúrate que los IDs coincidan con activity_login.xml
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        // Usamos la función de Task tradicional (signIn) de AuthService
        authService.signIn(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                } else {
                    val errorMessage = task.exception?.message ?: "Unknown login error."
                    Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }
    }

    // --- Lógica de Google Sign-In ---

    private fun signInWithGoogle() {
        val signInIntent = authService.getGoogleSignInIntent()
        googleSignInLauncher.launch(signInIntent)
    }

    // Launcher para manejar el resultado de la Activity de Google Sign-In
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Obtiene el token y lo usa para autenticar en Firebase
                val account = task.result
                val idToken = account.idToken ?: throw IllegalStateException("ID Token not found.")

                // Usamos una corrutina para la llamada suspendida
                lifecycleScope.launch {
                    authService.signInWithGoogle(idToken)
                        .onSuccess {
                            Toast.makeText(this@LoginActivity, "Google Sign-In successful.", Toast.LENGTH_SHORT).show()
                            navigateToMain()
                        }
                        .onFailure { exception ->
                            Toast.makeText(this@LoginActivity, "Firebase Error: ${exception.message}", Toast.LENGTH_LONG).show()
                        }
                }
            } catch (e: Exception) {
                // Manejar error general
                Toast.makeText(this, "Error processing Google Sign-In: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            // Usuario canceló
            Toast.makeText(this, "Google Sign-In canceled.", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Navegación ---

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Cierra LoginActivity para que el usuario no pueda volver atrás
    }
}