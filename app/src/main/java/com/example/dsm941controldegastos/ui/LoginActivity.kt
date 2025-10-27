package com.example.dsm941controldegastos.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dsm941controldegastos.databinding.ActivityLoginBinding
import com.example.dsm941controldegastos.service.AuthService
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var authService: AuthService
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authService = AuthService(this)

        binding.buttonLogin.setOnClickListener { loginUserWithEmail() }
        binding.textViewRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.buttonGoogleLogin.setOnClickListener { signInWithGoogle() }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = authService.getCurrentUser()
        if (currentUser != null) navigateToExpenses()
    }

    private fun loginUserWithEmail() {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        authService.signIn(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                navigateToExpenses()
            } else {
                Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = authService.getGoogleSignInIntent()
        googleSignInLauncher.launch(signInIntent)
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.result
                val idToken = account.idToken ?: throw IllegalStateException("ID Token not found")

                lifecycleScope.launch {
                    authService.signInWithGoogle(idToken)
                        .onSuccess {
                            Toast.makeText(this@LoginActivity, "Inicio con Google correcto", Toast.LENGTH_SHORT).show()
                            navigateToExpenses()
                        }
                        .onFailure { e ->
                            Toast.makeText(this@LoginActivity, "Error Firebase: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToExpenses() {
        startActivity(Intent(this, ExpensesActivity::class.java))
        finish()
    }
}
