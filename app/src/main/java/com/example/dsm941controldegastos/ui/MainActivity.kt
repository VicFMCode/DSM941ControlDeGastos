package com.example.dsm941controldegastos.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dsm941controldegastos.R // ¡Importante! Asegúrate que esta importación sea correcta.
import com.example.dsm941controldegastos.service.AuthService

class MainActivity : AppCompatActivity() {

    private lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cargar el layout principal (activity_main.xml)
        setContentView(R.layout.activity_main)

        // Inicializar el servicio de autenticación
        authService = AuthService(this)

        // Verificar si hay un usuario autenticado
        if (authService.getCurrentUser() == null) {
            navigateToLogin()
            return
        }

        // Mostrar un mensaje de bienvenida (solo por prueba)
        Toast.makeText(this, "Bienvenido: ${authService.getCurrentUser()?.email}", Toast.LENGTH_LONG).show()

        // --- Lógica de Cierre de Sesión (Logout) ---
        // Asume que el ID del botón es 'buttonLogout'
        val logoutButton = findViewById<Button>(R.id.buttonLogout)
        logoutButton.setOnClickListener {
            authService.signOut()
            Toast.makeText(this, "Sesión cerrada correctamente.", Toast.LENGTH_SHORT).show()
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        // Cierra todas las activities para evitar volver atrás
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}