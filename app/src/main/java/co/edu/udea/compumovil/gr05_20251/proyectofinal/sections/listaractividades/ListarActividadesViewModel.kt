package co.edu.udea.compumovil.gr05_20251.proyectofinal.sections.listaractividades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.udea.compumovil.gr05_20251.proyectofinal.data.RegistroActividad
import co.edu.udea.compumovil.gr05_20251.proyectofinal.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegistroConDetalles(
    val registro: RegistroActividad,
    val nombreActividad: String,
    val nombreSubactividad: String? = null
)

sealed class UiState {
    object Loading : UiState()
    data class Success(val registros: List<RegistroConDetalles>) : UiState()
    data class Error(val message: String) : UiState()
}

class ListarActividadesViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _selectedRegistro = MutableStateFlow<RegistroConDetalles?>(null)
    val selectedRegistro: StateFlow<RegistroConDetalles?> = _selectedRegistro.asStateFlow()

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    fun cargarRegistros(userId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                val registros = repository.obtenerRegistrosActividades(userId)
                val registrosConDetalles = mutableListOf<RegistroConDetalles>()

                for (registro in registros) {
                    val actividad = repository.obtenerActividadPorId(registro.actividadId)
                    val subactividad = if (registro.subactividadId != null) {
                        repository.obtenerSubactividadPorId(registro.subactividadId)
                    } else null

                    if (actividad != null) {
                        registrosConDetalles.add(
                            RegistroConDetalles(
                                registro = registro,
                                nombreActividad = actividad.nombre,
                                nombreSubactividad = subactividad?.nombre
                            )
                        )
                    }
                }

                _uiState.value = UiState.Success(registrosConDetalles)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al cargar los registros: ${e.message}")
            }
        }
    }

    fun mostrarDetalles(registro: RegistroConDetalles) {
        _selectedRegistro.value = registro
        _showDialog.value = true
    }

    fun ocultarDetalles() {
        _showDialog.value = false
        _selectedRegistro.value = null
    }

    fun formatearTiempo(horas: Int, minutos: Int): String {
        return when {
            horas > 0 && minutos > 0 -> "${horas}h ${minutos}m"
            horas > 0 -> "${horas}h"
            minutos > 0 -> "${minutos}m"
            else -> "0m"
        }
    }

    fun formatearFecha(fecha: String): String {
        return try {
            val parts = fecha.split("-")
            if (parts.size == 3) {
                "${parts[2]}/${parts[1]}/${parts[0]}"
            } else {
                fecha
            }
        } catch (e: Exception) {
            fecha
        }
    }
}