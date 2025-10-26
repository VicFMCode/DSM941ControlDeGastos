package com.example.dsm941controldegastos.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dsm941controldegastos.service.AuthService
import com.example.dsm941controldegastos.databinding.ActivityRegisterBinding // Asume que activity_register.xml existe
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    // Inicializamos el servicio pasándole el contexto
    private lateinit var authService: AuthService

    // View Binding: Asume que tienes un layout activity_register.xml
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar View Binding
        // Si tienes errores aquí, asegúrate de que el layout XML se llame 'activity_register.xml'
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authService = AuthService(this)

        // Configurar el listener para el botón de Registro (ID: buttonRegister)
        binding.buttonRegister.setOnClickListener {
            registerNewUser()
        }

        // Configurar el listener para volver a Iniciar Sesión (ID: textViewGoToLogin)
        binding.textViewGoToLogin.setOnClickListener {
            finish() // Cierra esta Activity para volver a LoginActivity (que ya está en el stack)
        }
    }

    private fun registerNewUser() {
        // IDs de EditText: Asegúrate de que coincidan con activity_register.xml
        val email = binding.editTextRegisterEmail.text.toString().trim()
        val password = binding.editTextRegisterPassword.text.toString().trim()
        val confirmPassword = binding.editTextRegisterConfirmPassword.text.toString().trim()

        // 1. Validaciones
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

        // 2. Usar Corrutinas para llamar a la función suspendida de registro
        lifecycleScope.launch {
            authService.signUpWithEmail(email, password)
                .onSuccess {
                    Toast.makeText(this@RegisterActivity, "¡Registro exitoso! Iniciando sesión...", Toast.LENGTH_LONG).show()
                    navigateToMain()
                }
                .onFailure { exception ->
                    val errorMessage = exception.message ?: "Error desconocido al registrar."
                    Toast.makeText(this@RegisterActivity, "Error de registro: $errorMessage", Toast.LENGTH_LONG).show()
                }
        }
    }

    // Función de navegación compartida
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        // Estas flags aseguran que las Activities de autenticación sean eliminadas del stack
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}