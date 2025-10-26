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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dsm941controldegastos.data.FirestoreRepository
import com.example.dsm941controldegastos.data.Gasto
import com.example.dsm941controldegastos.service.AuthService
import java.text.SimpleDateFormat
import java.util.*

class ExpensesActivity : ComponentActivity() {

    private val repo = FirestoreRepository()
    private lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instanciamos AuthService con el contexto actual
        authService = AuthService(this)

        setContent {
            ExpensesScreen(
                onAddClick = { startActivity(Intent(this, AddExpenseActivity::class.java)) }
            )
        }
    }

    @Composable
    fun ExpensesScreen(onAddClick: () -> Unit) {
        val uid = authService.getCurrentUserUid() // ✅ Ahora funciona correctamente
        var gastos by remember { mutableStateOf<List<Gasto>>(emptyList()) }

        LaunchedEffect(uid) {
            if (!uid.isNullOrBlank()) {
                repo.observarGastos(uid) { lista -> gastos = lista }
            }
        }

        val totalMensual = repo.calcularTotalMensual(gastos)

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = onAddClick) {
                    Text("+")
                }
            }
        ) { padding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text("Gastos personales", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                Text("Total mensual: $${"%.2f".format(totalMensual)}")
                Spacer(Modifier.height(12.dp))

                if (gastos.isEmpty()) {
                    Text("No hay gastos registrados aún.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
            Modifier
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
