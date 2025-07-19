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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.udea.compumovil.gr05_20251.proyectofinal.ui.theme.GreenColor

class RegistrarActividadActivity : ComponentActivity() {

    companion object {
        const val EXTRA_REGISTRO_ID = "extra_registro_id"
    }

    private val viewModel: RegistrarActividadViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val registroId = intent.getStringExtra(EXTRA_REGISTRO_ID)
        viewModel.iniciarFormulario(registroId)

        setContent {
            MaterialTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val context = LocalContext.current

                val screenTitle = if (registroId != null) "Editar Registro" else "Registrar Actividad"

                LaunchedEffect(uiState.errorMessage) {
                    uiState.errorMessage?.let { error ->
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        viewModel.limpiarError()
                    }
                }

                LaunchedEffect(uiState.guardadoExitoso) {
                    if (uiState.guardadoExitoso) {
                        Toast.makeText(
                            context,
                            if (registroId != null) "Registro actualizado exitosamente" else "Registro guardado exitosamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(screenTitle, color = Color.White) },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Volver",
                                        tint = Color.White
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = GreenColor,
                                titleContentColor = Color.White,
                                navigationIconContentColor = Color.White
                            )
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
