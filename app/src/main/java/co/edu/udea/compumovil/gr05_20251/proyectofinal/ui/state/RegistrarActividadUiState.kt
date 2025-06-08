package co.edu.udea.compumovil.gr05_20251.proyectofinal.ui.state

import co.edu.udea.compumovil.gr05_20251.proyectofinal.data.Actividad
import co.edu.udea.compumovil.gr05_20251.proyectofinal.data.Subactividad

// UIState para el ViewModel
data class RegistrarActividadUiState(
    val actividades: List<Actividad> = emptyList(),
    val subactividades: List<Subactividad> = emptyList(),
    val actividadSeleccionada: Actividad? = null,
    val subactividadSeleccionada: Subactividad? = null,
    val fecha: String = "",
    val horas: String = "",
    val minutos: String = "",
    val comentarios: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val guardadoExitoso: Boolean = false
)