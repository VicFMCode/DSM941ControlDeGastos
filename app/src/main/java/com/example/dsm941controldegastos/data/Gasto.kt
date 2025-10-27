package com.example.dsm941controldegastos.data

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Gasto(
    @DocumentId
    val id: String? = null,
    val nombre: String = "",
    val monto: Double = 0.0,
    val categoria: String = "",
    val fecha: Date = Date(),
    val userId: String = ""
) {
    companion object {
        val CATEGORIAS = listOf(
            "Comida",
            "Transporte",
            "Hogar",
            "Entretenimiento",
            "Salud",
            "Educación",
            "Otros"
        )
    }
}
