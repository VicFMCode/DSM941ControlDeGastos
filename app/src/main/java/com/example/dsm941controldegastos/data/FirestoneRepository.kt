package com.example.dsm941controldegastos.data

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.*

class FirestoreRepository {

    private val db = Firebase.firestore

    private fun userGastosRef(uid: String) =
        db.collection("users").document(uid).collection("gastos")

    suspend fun agregarGasto(uid: String, gasto: Gasto) {
        val ref = userGastosRef(uid).document()
        val gastoConId = gasto.copy(id = ref.id, userId = uid)
        ref.set(gastoConId).await()
    }

    suspend fun eliminarGasto(uid: String, id: String) {
        userGastosRef(uid).document(id).delete().await()
    }

    fun observarGastos(uid: String, listener: (List<Gasto>) -> Unit) =
        userGastosRef(uid)
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    listener(emptyList())
                    return@addSnapshotListener
                }
                val gastos = snapshot?.toObjects(Gasto::class.java) ?: emptyList()
                listener(gastos)
            }

    fun calcularTotalMensual(gastos: List<Gasto>): Double {
        val cal = Calendar.getInstance()
        val mesActual = cal.get(Calendar.MONTH)
        val añoActual = cal.get(Calendar.YEAR)
        return gastos.filter {
            cal.time = it.fecha
            cal.get(Calendar.MONTH) == mesActual && cal.get(Calendar.YEAR) == añoActual
        }.sumOf { it.monto }
    }
}
