package com.example.dsm941controldegastos.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dsm941controldegastos.service.AuthService
import com.example.dsm941controldegastos.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await // ✅ CORRECCIÓN

class RegisterActivity : AppCompatActivity() {

    private lateinit var authService: AuthService
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authService = AuthService(this)

        binding.buttonRegister.setOnClickListener {
            registerNewUser()
        }

        binding.textViewGoToLogin.setOnClickListener {
            finish()
        }
    }

    private fun registerNewUser() {
        val email = binding.editTextRegisterEmail.text.toString().trim()
        val password = binding.editTextRegisterPassword.text.toString().trim()
        val confirmPassword = binding.editTextRegisterConfirmPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                authService.signUp(email, password).await() 
                Toast.makeText(this@RegisterActivity, "¡Registro exitoso! Iniciando sesión...", Toast.LENGTH_LONG).show()
                navigateToExpenses()
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Error de registro: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToExpenses() {
        val intent = Intent(this, ExpensesActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
