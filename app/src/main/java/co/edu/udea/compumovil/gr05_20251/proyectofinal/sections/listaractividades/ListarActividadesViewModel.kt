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
    val nombreSubactividad: String? = null,
    val registroId: String = "" // Agregamos el ID del documento
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

    private val _showDeleteConfirmation = MutableStateFlow(false)
    val showDeleteConfirmation: StateFlow<Boolean> = _showDeleteConfirmation.asStateFlow()

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    private val _deleteMessage = MutableStateFlow<String?>(null)
    val deleteMessage: StateFlow<String?> = _deleteMessage.asStateFlow()

    fun cargarRegistros(userId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                val registrosConIds = repository.obtenerRegistrosActividades(userId)
                val registrosConDetalles = mutableListOf<RegistroConDetalles>()

                for ((documentId, registro) in registrosConIds) {
                    val actividad = repository.obtenerActividadPorId(registro.actividadId)
                    val subactividad = if (registro.subactividadId != null) {
                        repository.obtenerSubactividadPorId(registro.subactividadId)
                    } else null

                    if (actividad != null) {
                        registrosConDetalles.add(
                            RegistroConDetalles(
                                registro = registro,
                                nombreActividad = actividad.nombre,
                                nombreSubactividad = subactividad?.nombre,
                                registroId = documentId
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

    fun mostrarConfirmacionBorrar() {
        _showDeleteConfirmation.value = true
    }

    fun ocultarConfirmacionBorrar() {
        _showDeleteConfirmation.value = false
    }

    fun borrarRegistro(userId: String) {
        val registro = _selectedRegistro.value ?: return

        viewModelScope.launch {
            _isDeleting.value = true

            try {
                val result = repository.eliminarRegistroActividad(registro.registroId)

                if (result.isSuccess) {
                    _deleteMessage.value = "Registro eliminado exitosamente"

                    // Ocultar diálogos ANTES de actualizar la lista
                    _showDialog.value = false
                    _showDeleteConfirmation.value = false
                    _selectedRegistro.value = null

                    // Actualizar la lista localmente eliminando el registro
                    val currentState = _uiState.value
                    if (currentState is UiState.Success) {
                        val updatedList = currentState.registros.filter { it.registroId != registro.registroId }
                        _uiState.value = UiState.Success(updatedList)
                    }

                } else {
                    _deleteMessage.value = "Error al eliminar el registro"
                }
            } catch (e: Exception) {
                _deleteMessage.value = "Error al eliminar el registro: ${e.message}"
            } finally {
                _isDeleting.value = false
            }
        }
    }

    fun limpiarMensajeBorrar() {
        _deleteMessage.value = null
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