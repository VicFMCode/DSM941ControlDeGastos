package com.example.dsm941controldegastos.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions


class AddExpenseActivity : ComponentActivity() {

    private val repo = FirestoreRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddExpenseScreen(
                onSave = { gasto ->
                    val uid = AuthService(this).getCurrentUser()?.uid ?: return@AddExpenseScreen
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            repo.agregarGasto(uid, gasto)
                            finish()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
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

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Top
        ) {
            Text("Nuevo gasto", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del gasto") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = montoStr,
                onValueChange = { montoStr = it },
                label = { Text("Monto") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            // Selector de categoría simple
            var expanded by remember { mutableStateOf(false) }
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
                    Gasto.CATEGORIAS.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                categoria = it
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {
                    val monto = montoStr.toDoubleOrNull() ?: 0.0
                    if (nombre.isNotBlank() && monto > 0) {
                        onSave(
                            Gasto(
                                nombre = nombre.trim(),
                                monto = monto,
                                categoria = categoria,
                                fecha = Date()
                            )
                        )
                    }
                }) {
                    Text("Guardar")
                }
                OutlinedButton(onClick = onCancel) {
                    Text("Cancelar")
                }
            }
        }
    }
}
