package co.edu.udea.compumovil.gr05_20251.proyectofinal.data

// RegistroActividad.kt
data class RegistroActividad(
    val id: String = "",
    val actividadId: String = "",
    val subactividadId: String? = null,
    val fecha: String = "", // Formato: "yyyy-MM-dd"
    val horas: Int = 0,
    val minutos: Int = 0,
    val comentarios: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
