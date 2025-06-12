package co.edu.udea.compumovil.gr05_20251.proyectofinal.sections.registraractividad

// RegistrarActividadViewModel.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.udea.compumovil.gr05_20251.proyectofinal.data.Actividad
import co.edu.udea.compumovil.gr05_20251.proyectofinal.data.RegistroActividad
import co.edu.udea.compumovil.gr05_20251.proyectofinal.data.Subactividad
import co.edu.udea.compumovil.gr05_20251.proyectofinal.repository.FirebaseRepository
import co.edu.udea.compumovil.gr05_20251.proyectofinal.ui.state.RegistrarActividadUiState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RegistrarActividadViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    private val auth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow(RegistrarActividadUiState())
    val uiState: StateFlow<RegistrarActividadUiState> = _uiState.asStateFlow()

    init {
        // We will load activities from an external function now, possibly after receiving a registroId
        // Establecer fecha actual por defecto si no es un modo de edición
        val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        _uiState.value = _uiState.value.copy(fecha = fechaActual)
    }

    // Function to initialize the form for a new registration or load data for editing
    fun iniciarFormulario(registroId: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true) // Start loading state

            try {
                // Load all activities first
                val actividades = repository.obtenerActividades()

                // If a registroId is provided, load the existing record for editing
                if (registroId != null) {
                    val (id, registro) = repository.obtenerRegistroActividadPorId(registroId)
                        ?: throw Exception("Registro no encontrado")

                    // Find the selected actividad and subactividad by ID
                    val selectedActividad = actividades.find { it.id == registro.actividadId }
                    var selectedSubactividad: Subactividad? = null
                    if (registro.subactividadId != null && selectedActividad != null && selectedActividad.tieneSubactividades) {
                        val subactividades = repository.obtenerSubactividadesPorActividad(selectedActividad.id)
                        selectedSubactividad = subactividades.find { it.id == registro.subactividadId }
                        _uiState.value = _uiState.value.copy(subactividades = subactividades) // Populate subactividades list
                    }

                    _uiState.value = _uiState.value.copy(
                        registroId = id,
                        actividades = actividades,
                        actividadSeleccionada = selectedActividad,
                        subactividadSeleccionada = selectedSubactividad,
                        fecha = registro.fecha,
                        horas = registro.horas.toString(),
                        minutos = registro.minutos.toString(),
                        comentarios = registro.comentarios,
                        isLoading = false,
                        errorMessage = null
                    )
                } else {
                    // If no registroId, it's a new registration, just populate activities and default date
                    val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    _uiState.value = _uiState.value.copy(
                        registroId = null, // Ensure ID is null for new records
                        actividades = actividades,
                        actividadSeleccionada = null,
                        subactividadSeleccionada = null,
                        subactividades = emptyList(), // Clear subactivities for new record
                        fecha = fechaActual,
                        horas = "",
                        minutos = "",
                        comentarios = "",
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar datos: ${e.message}"
                )
            }
        }
    }


    fun seleccionarActividad(actividad: Actividad) {
        _uiState.value = _uiState.value.copy(
            actividadSeleccionada = actividad,
            subactividadSeleccionada = null,
            subactividades = emptyList() // Clear previous subactivities
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
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "Usuario no autenticado")
            return
        }

        // Validar campos obligatorios
        if (!validarCampos(estado)) {
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val registro = RegistroActividad(
                    userId = currentUserId,
                    actividadId = estado.actividadSeleccionada!!.id,
                    subactividadId = estado.subactividadSeleccionada?.id,
                    fecha = estado.fecha,
                    horas = estado.horas.toInt(),
                    minutos = estado.minutos.toInt(),
                    comentarios = estado.comentarios,
                    // For update, we want to keep the original timestamp, for new, let it be System.currentTimeMillis()
                    timestamp = estado.registroId?.let { // If it's an edit, try to preserve original timestamp
                        (repository.obtenerRegistroActividadPorId(it)?.second?.timestamp ?: System.currentTimeMillis())
                    } ?: System.currentTimeMillis()
                )

                val resultado = if (estado.registroId != null) {
                    repository.actualizarRegistroActividad(estado.registroId, registro)
                } else {
                    repository.guardarRegistroActividad(registro)
                }


                if (resultado.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        guardadoExitoso = true,
                        errorMessage = null
                    )
                    // No limpiar formulario immediately after success if activity is closing.
                    // The activity will handle finishing itself.
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
        limpiarFormulario() // Clear form after success is acknowledged
    }

    private fun limpiarFormulario() {
        val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        _uiState.value = _uiState.value.copy(
            registroId = null, // Crucial: clear ID for new records
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
