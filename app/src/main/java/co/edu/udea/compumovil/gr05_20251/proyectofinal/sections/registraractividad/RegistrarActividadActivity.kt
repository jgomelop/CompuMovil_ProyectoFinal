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

    // Define a key for the extra in the Intent
    companion object {
        const val EXTRA_REGISTRO_ID = "extra_registro_id"
    }

    private val viewModel: RegistrarActividadViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the registroId from the Intent, if it exists
        val registroId = intent.getStringExtra(EXTRA_REGISTRO_ID)

        // Initialize the form in the ViewModel with the registroId (or null for new)
        viewModel.iniciarFormulario(registroId)

        setContent {
            MaterialTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val context = LocalContext.current

                // Determine the title based on whether we are editing or creating
                val screenTitle = if (registroId != null) "Editar Registro" else "Registrar Actividad"

                // Show toast for errors
                LaunchedEffect(uiState.errorMessage) {
                    uiState.errorMessage?.let { error ->
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        viewModel.limpiarError()
                    }
                }

                // Show success toast and close activity
                LaunchedEffect(uiState.guardadoExitoso) {
                    if (uiState.guardadoExitoso) {
                        Toast.makeText(context, if (registroId != null) "Registro actualizado exitosamente" else "Registro guardado exitosamente", Toast.LENGTH_SHORT).show()
                        finish() // Close the activity after saving/updating
                    }
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(screenTitle) },
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
