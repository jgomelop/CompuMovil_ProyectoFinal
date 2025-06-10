package co.edu.udea.compumovil.gr05_20251.proyectofinal.sections.listaractividades

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import co.edu.udea.compumovil.gr05_20251.proyectofinal.repository.FirebaseRepository
import co.edu.udea.compumovil.gr05_20251.proyectofinal.ui.theme.ProyectoFinalTheme
import com.google.firebase.auth.FirebaseAuth

class ListarActividadesActivity : ComponentActivity() {

    private lateinit var viewModel: ListarActividadesViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Inicializar ViewModel
        val repository = FirebaseRepository()
        val factory = ListarActividadesViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ListarActividadesViewModel::class.java]

        setContent {
            ProyectoFinalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        ListarActividadesScreen(
                            viewModel = viewModel
                        )
                    } else {
                        // Redirigir al login si no hay usuario autenticado
                        finish()
                    }
                }
            }
        }
    }
}

// Factory para el ViewModel
class ListarActividadesViewModelFactory(
    private val repository: FirebaseRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListarActividadesViewModel::class.java)) {
            return ListarActividadesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
