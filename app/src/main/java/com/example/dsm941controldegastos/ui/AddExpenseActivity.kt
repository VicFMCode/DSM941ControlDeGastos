package com.example.dsm941controldegastos.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.dsm941controldegastos.data.FirestoreRepository
import com.example.dsm941controldegastos.data.Gasto
import com.example.dsm941controldegastos.service.AuthService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class) // suprime el warning de TopAppBar
class AddExpenseActivity : ComponentActivity() {

    private lateinit var repo: FirestoreRepository
    private lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repo = FirestoreRepository()
        authService = AuthService(this)

        setContent {
            MaterialTheme {
                AddExpenseScreen(
                    onSave = { gasto ->
                        val uid = authService.getCurrentUserUid()
                        if (uid != null) {
                            // 🔹 Usamos corrutina para llamar suspend fun agregarGasto()
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    repo.agregarGasto(uid, gasto)
                                    runOnUiThread {
                                        Toast.makeText(
                                            this@AddExpenseActivity,
                                            "Gasto agregado correctamente",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    }
                                } catch (e: Exception) {
                                    runOnUiThread {
                                        Toast.makeText(
                                            this@AddExpenseActivity,
                                            "Error al guardar: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(this, "No hay usuario autenticado", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onCancel = { finish() }
                )
            }
        }
    }

    @Composable
    fun AddExpenseScreen(onSave: (Gasto) -> Unit, onCancel: () -> Unit) {
        var nombre by remember { mutableStateOf("") }
        var montoStr by remember { mutableStateOf("") }
        var categoria by remember { mutableStateOf(Gasto.CATEGORIAS.first()) }
        var expanded by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Agregar gasto") }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del gasto") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = montoStr,
                    onValueChange = { montoStr = it },
                    label = { Text("Monto") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Box {
                    OutlinedTextField(
                        value = categoria,
                        onValueChange = {},
                        label = { Text("Categoría") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true },
                        readOnly = true
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        Gasto.CATEGORIAS.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    categoria = cat
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            val monto = montoStr.toDoubleOrNull()
                            if (nombre.isBlank() || monto == null) {
                                Toast.makeText(this@AddExpenseActivity, "Campos inválidos", Toast.LENGTH_SHORT).show()
                            } else {
                                onSave(
                                    Gasto(
                                        nombre = nombre,
                                        monto = monto,
                                        categoria = categoria,
                                        fecha = Date()
                                    )
                                )
                            }
                        }
                    ) {
                        Text("Guardar")
                    }

                    OutlinedButton(onClick = onCancel) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}
