package co.edu.udea.compumovil.gr05_20251.proyectofinal.sections.registraractividad

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun RegistrarActividadScreen(viewModel: RegistrarActividadViewModel) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Sección Registrar actividad")
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrarScreenPreview() {
    // Si no necesitas lógica en el preview, puedes usar un ViewModel falso
    val fakeViewModel = remember { RegistrarActividadViewModel() }
    RegistrarActividadScreen(viewModel = fakeViewModel)
}

