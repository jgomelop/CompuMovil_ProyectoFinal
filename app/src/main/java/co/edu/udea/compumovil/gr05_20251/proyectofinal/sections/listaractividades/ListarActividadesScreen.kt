package co.edu.udea.compumovil.gr05_20251.proyectofinal.sections.listaractividades

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

class ListarActividadesScreen {
}

@Composable
fun ListarActividadesScreen(viewModel: ListarActividadesViewModel) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Sección Lista de Actividades")
    }
}