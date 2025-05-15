package co.edu.udea.compumovil.gr05_20251.proyectofinal.sections.listaractividades

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.activity.compose.setContent
import co.edu.udea.compumovil.gr05_20251.proyectofinal.ui.theme.ProyectoFinalTheme

class ListarActividadesActivity : ComponentActivity() {
    private val viewModel: ListarActividadesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoFinalTheme {
                ListarActividadesScreen(viewModel)
            }
        }
    }
}
