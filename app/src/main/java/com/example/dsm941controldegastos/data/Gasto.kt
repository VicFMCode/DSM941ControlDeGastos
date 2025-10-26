package com.example.dsm941controldegastos.data

import com.google.firebase.firestore.DocumentId
import java.util.Date

/**
 * Clase de datos que representa un Gasto personal.
 *
 * @param id El ID del documento de Firestore. Se genera automáticamente y se usa para actualizaciones/eliminaciones.
 * @param nombre El nombre o descripción breve del gasto (e.g., "Pago de luz").
 * @param monto El valor monetario del gasto.
 * @param categoria La categoría del gasto (e.g., "Hogar", "Transporte").
 * @param fecha La fecha en que se realizó el gasto.
 * @param userId El ID del usuario asociado a este gasto (para seguridad en Firestore).
 */
data class Gasto(
    // Anotación para indicar a Firestore que este campo debe mapearse con el ID del documento
    @DocumentId
    val id: String? = null,
    val nombre: String = "",
    val monto: Double = 0.0,
    val categoria: String = "",
    // Usamos java.util.Date para un mapeo fácil y consistente con Firestore Timestamp
    val fecha: Date = Date(),
    val userId: String = ""
) {
    // Definimos categorías estáticas que usaremos en la UI para un fácil manejo
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
