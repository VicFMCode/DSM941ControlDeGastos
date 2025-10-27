package com.example.dsm941controldegastos.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dsm941controldegastos.data.FirestoreRepository
import com.example.dsm941controldegastos.data.Gasto
import com.example.dsm941controldegastos.service.AuthService
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class) // Aplica solo a Material3
class ExpensesActivity : ComponentActivity() {

    private val repo = FirestoreRepository()
    private lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authService = AuthService(this)

        setContent {
            MaterialTheme {
                ExpensesScreen(
                    onAddClick = { startActivity(Intent(this, AddExpenseActivity::class.java)) },
                    onLogoutClick = {
                        authService.signOut()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }

    @Composable
    fun ExpensesScreen(onAddClick: () -> Unit, onLogoutClick: () -> Unit) {
        val uid = authService.getCurrentUserUid()
        var gastos by remember { mutableStateOf<List<Gasto>>(emptyList()) }
        var loading by remember { mutableStateOf(true) }

        LaunchedEffect(uid) {
            if (!uid.isNullOrBlank()) {
                try {
                    repo.observarGastos(uid) { lista ->
                        gastos = lista
                        loading = false
                    }
                } catch (e: Exception) {
                    // Evita que la UI se bloquee
                    gastos = emptyList()
                    loading = false
                }
            } else {
                loading = false
            }
        }

        val totalMensual = repo.calcularTotalMensual(gastos)

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Gastos personales") },
                    actions = {
                        TextButton(onClick = onLogoutClick) {
                            Text("Cerrar sesión")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onAddClick) {
                    Text("+")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text("Total mensual: $${"%.2f".format(totalMensual)}")
                Spacer(Modifier.height(12.dp))

                when {
                    loading -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                    uid.isNullOrBlank() -> Text("No hay usuario logueado.", style = MaterialTheme.typography.bodyMedium)
                    gastos.isEmpty() -> Text("No hay gastos registrados.", style = MaterialTheme.typography.bodyMedium)
                    else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(gastos) { gasto ->
                            GastoItem(gasto)
                            Divider()
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun GastoItem(gasto: Gasto) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(gasto.nombre, style = MaterialTheme.typography.titleMedium)
                Text("${gasto.categoria} - ${sdf.format(gasto.fecha)}")
            }
            Text("$${"%.2f".format(gasto.monto)}", style = MaterialTheme.typography.titleMedium)
        }
    }
}
