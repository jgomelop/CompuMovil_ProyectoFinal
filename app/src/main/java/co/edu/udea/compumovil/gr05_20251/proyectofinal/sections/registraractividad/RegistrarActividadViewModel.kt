package co.edu.udea.compumovil.gr05_20251.proyectofinal.sections.registraractividad

// RegistrarActividadViewModel.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.udea.compumovil.gr05_20251.proyectofinal.data.Actividad
import co.edu.udea.compumovil.gr05_20251.proyectofinal.data.RegistroActividad
import co.edu.udea.compumovil.gr05_20251.proyectofinal.data.Subactividad
import co.edu.udea.compumovil.gr05_20251.proyectofinal.repository.FirebaseRepository
import co.edu.udea.compumovil.gr05_20251.proyectofinal.ui.state.RegistrarActividadUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RegistrarActividadViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _uiState = MutableStateFlow(RegistrarActividadUiState())
    val uiState: StateFlow<RegistrarActividadUiState> = _uiState.asStateFlow()

    init {
        cargarActividades()
        // Establecer fecha actual por defecto
        val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        _uiState.value = _uiState.value.copy(fecha = fechaActual)
    }

    private fun cargarActividades() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val actividades = repository.obtenerActividades()
                _uiState.value = _uiState.value.copy(
                    actividades = actividades,
                    isLoading = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar actividades: ${e.message}"
                )
            }
        }
    }

    fun seleccionarActividad(actividad: Actividad) {
        _uiState.value = _uiState.value.copy(
            actividadSeleccionada = actividad,
            subactividadSeleccionada = null,
            subactividades = emptyList()
        )

        // Cargar subactividades si la actividad las tiene
        if (actividad.tieneSubactividades) {
            cargarSubactividades(actividad.id)
        }
    }

    private fun cargarSubactividades(actividadId: String) {
        viewModelScope.launch {
            try {
                val subactividades = repository.obtenerSubactividadesPorActividad(actividadId)
                _uiState.value = _uiState.value.copy(subactividades = subactividades)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al cargar subactividades: ${e.message}"
                )
            }
        }
    }

    fun seleccionarSubactividad(subactividad: Subactividad) {
        _uiState.value = _uiState.value.copy(subactividadSeleccionada = subactividad)
    }

    fun actualizarFecha(fecha: String) {
        _uiState.value = _uiState.value.copy(fecha = fecha)
    }

    fun actualizarHoras(horas: String) {
        _uiState.value = _uiState.value.copy(horas = horas)
    }

    fun actualizarMinutos(minutos: String) {
        _uiState.value = _uiState.value.copy(minutos = minutos)
    }

    fun actualizarComentarios(comentarios: String) {
        _uiState.value = _uiState.value.copy(comentarios = comentarios)
    }

    fun guardarRegistro() {
        val estado = _uiState.value

        // Validar campos obligatorios
        if (!validarCampos(estado)) {
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val registro = RegistroActividad(
                    actividadId = estado.actividadSeleccionada!!.id,
                    subactividadId = estado.subactividadSeleccionada?.id,
                    fecha = estado.fecha,
                    horas = estado.horas.toInt(),
                    minutos = estado.minutos.toInt(),
                    comentarios = estado.comentarios
                )

                val resultado = repository.guardarRegistroActividad(registro)

                if (resultado.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        guardadoExitoso = true,
                        errorMessage = null
                    )
                    limpiarFormulario()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al guardar: ${resultado.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error inesperado: ${e.message}"
                )
            }
        }
    }

    private fun validarCampos(estado: RegistrarActividadUiState): Boolean {
        when {
            estado.actividadSeleccionada == null -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Debe seleccionar una actividad")
                return false
            }
            estado.actividadSeleccionada.tieneSubactividades && estado.subactividadSeleccionada == null -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Debe seleccionar una subactividad")
                return false
            }
            estado.fecha.isEmpty() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Debe seleccionar una fecha")
                return false
            }
            estado.horas.isEmpty() || estado.horas.toIntOrNull() == null -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Debe ingresar las horas (0-23)")
                return false
            }
            estado.horas.toInt() !in 0..23 -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Las horas deben estar entre 0 y 23")
                return false
            }
            estado.minutos.isEmpty() || estado.minutos.toIntOrNull() == null -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Debe ingresar los minutos (0-59)")
                return false
            }
            estado.minutos.toInt() !in 0..59 -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Los minutos deben estar entre 0 y 59")
                return false
            }
            estado.comentarios.isEmpty() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Debe agregar un comentario")
                return false
            }
        }
        return true
    }

    fun limpiarError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun limpiarEstadoGuardado() {
        _uiState.value = _uiState.value.copy(guardadoExitoso = false)
    }

    private fun limpiarFormulario() {
        val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        _uiState.value = _uiState.value.copy(
            actividadSeleccionada = null,
            subactividadSeleccionada = null,
            subactividades = emptyList(),
            fecha = fechaActual,
            horas = "",
            minutos = "",
            comentarios = ""
        )
    }
}