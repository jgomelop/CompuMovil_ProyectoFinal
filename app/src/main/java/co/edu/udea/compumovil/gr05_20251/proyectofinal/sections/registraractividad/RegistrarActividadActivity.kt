package co.edu.udea.compumovil.gr05_20251.proyectofinal.sections.registraractividad

// RegistrarActividadActivity.kt
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle

class RegistrarActividadActivity : ComponentActivity() {
    private val viewModel: RegistrarActividadViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val context = LocalContext.current

                // Mostrar toast para errores
                LaunchedEffect(uiState.errorMessage) {
                    uiState.errorMessage?.let { error ->
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        viewModel.limpiarError()
                    }
                }

                // Mostrar toast de éxito y cerrar activity
                LaunchedEffect(uiState.guardadoExitoso) {
                    if (uiState.guardadoExitoso) {
                        Toast.makeText(context, "Registro guardado exitosamente", Toast.LENGTH_SHORT).show()
                        finish() // Cerrar la actividad después de guardar
                    }
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Registrar Actividad") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Volver"
                                    )
                                }
                            }
                        )
                    }
                ) { paddingValues ->
                    RegistrarActividadScreen(
                        uiState = uiState,
                        onActividadSeleccionada = viewModel::seleccionarActividad,
                        onSubactividadSeleccionada = viewModel::seleccionarSubactividad,
                        onFechaChanged = viewModel::actualizarFecha,
                        onHorasChanged = viewModel::actualizarHoras,
                        onMinutosChanged = viewModel::actualizarMinutos,
                        onComentariosChanged = viewModel::actualizarComentarios,
                        onGuardarClick = viewModel::guardarRegistro,
                        onErrorDismissed = viewModel::limpiarError,
                        onSuccessDismissed = viewModel::limpiarEstadoGuardado,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                }
            }
        }
    }
}