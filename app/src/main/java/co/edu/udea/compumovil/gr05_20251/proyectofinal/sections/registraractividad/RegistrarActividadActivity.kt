package co.edu.udea.compumovil.gr05_20251.proyectofinal.sections.registraractividad

import co.edu.udea.compumovil.gr05_20251.proyectofinal.ui.theme.ProyectoFinalTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.activity.compose.setContent

class RegistrarActividadActivity : ComponentActivity() {
    private val viewModel: RegistrarActividadViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoFinalTheme {
                RegistrarActividadScreen(viewModel)
            }
        }
    }
}
