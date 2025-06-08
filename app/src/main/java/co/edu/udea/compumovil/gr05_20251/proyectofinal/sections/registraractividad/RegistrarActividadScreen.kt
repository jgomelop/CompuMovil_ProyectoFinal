package co.edu.udea.compumovil.gr05_20251.proyectofinal.sections.registraractividad

// RegistrarActividadScreen.kt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import co.edu.udea.compumovil.gr05_20251.proyectofinal.data.Actividad
import co.edu.udea.compumovil.gr05_20251.proyectofinal.data.Subactividad
import co.edu.udea.compumovil.gr05_20251.proyectofinal.ui.state.RegistrarActividadUiState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarActividadScreen(
    uiState: RegistrarActividadUiState,
    onActividadSeleccionada: (Actividad) -> Unit,
    onSubactividadSeleccionada: (Subactividad) -> Unit,
    onFechaChanged: (String) -> Unit,
    onHorasChanged: (String) -> Unit,
    onMinutosChanged: (String) -> Unit,
    onComentariosChanged: (String) -> Unit,
    onGuardarClick: () -> Unit,
    onErrorDismissed: () -> Unit,
    onSuccessDismissed: () -> Unit,
    modifier: Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var expandedActividad by remember { mutableStateOf(false) }
    var expandedSubactividad by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título
        Text(
            text = "Registrar Actividad",
            style = MaterialTheme.typography.headlineMedium
        )

        // Dropdown Actividad
        ExposedDropdownMenuBox(
            expanded = expandedActividad,
            onExpandedChange = { expandedActividad = !expandedActividad }
        ) {
            OutlinedTextField(
                value = uiState.actividadSeleccionada?.nombre ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Actividad *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedActividad) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandedActividad,
                onDismissRequest = { expandedActividad = false }
            ) {
                uiState.actividades.forEach { actividad ->
                    DropdownMenuItem(
                        text = { Text(actividad.nombre) },
                        onClick = {
                            onActividadSeleccionada(actividad)
                            expandedActividad = false
                        }
                    )
                }
            }
        }

        // Dropdown Subactividad (solo si la actividad seleccionada tiene subactividades)
        if (uiState.actividadSeleccionada?.tieneSubactividades == true) {
            ExposedDropdownMenuBox(
                expanded = expandedSubactividad,
                onExpandedChange = { expandedSubactividad = !expandedSubactividad }
            ) {
                OutlinedTextField(
                    value = uiState.subactividadSeleccionada?.nombre ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Subactividad *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSubactividad) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedSubactividad,
                    onDismissRequest = { expandedSubactividad = false }
                ) {
                    uiState.subactividades.forEach { subactividad ->
                        DropdownMenuItem(
                            text = { Text(subactividad.nombre) },
                            onClick = {
                                onSubactividadSeleccionada(subactividad)
                                expandedSubactividad = false
                            }
                        )
                    }
                }
            }
        }

        // Campo Fecha
        OutlinedTextField(
            value = uiState.fecha,
            onValueChange = {},
            readOnly = true,
            label = { Text("Fecha *") },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Campos Horas y Minutos en fila
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = uiState.horas,
                onValueChange = onHorasChanged,
                label = { Text("Horas *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = uiState.minutos,
                onValueChange = onMinutosChanged,
                label = { Text("Minutos *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        // Campo Comentarios
        OutlinedTextField(
            value = uiState.comentarios,
            onValueChange = onComentariosChanged,
            label = { Text("Comentarios *") },
            minLines = 3,
            maxLines = 5,
            modifier = Modifier.fillMaxWidth()
        )

        // Botón Guardar
        Button(
            onClick = onGuardarClick,
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (uiState.isLoading) "Guardando..." else "Guardar Registro")
        }
    }

    // DatePicker
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val fecha = formatter.format(Date(millis))
                            onFechaChanged(fecha)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Snackbar para errores
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            // Aquí puedes mostrar un Snackbar o manejar el error
            // Por simplicidad, solo limpiamos el error después de un tiempo
            kotlinx.coroutines.delay(3000)
            onErrorDismissed()
        }
    }

    // Snackbar para éxito
    if (uiState.guardadoExitoso) {
        LaunchedEffect(uiState.guardadoExitoso) {
            // Mostrar mensaje de éxito y limpiar estado
            kotlinx.coroutines.delay(2000)
            onSuccessDismissed()
        }
    }
}

