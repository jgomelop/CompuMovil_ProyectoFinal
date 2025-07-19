package co.edu.udea.compumovil.gr05_20251.proyectofinal

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import co.edu.udea.compumovil.gr05_20251.proyectofinal.login.LoginActivity
import co.edu.udea.compumovil.gr05_20251.proyectofinal.ui.theme.ProyectoFinalTheme
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cambiar a español por ejemplo:
        val locale = Locale("es") // o "fr", "de", etc.
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Verificar si el usuario está autenticado
        if (auth.currentUser == null) {
            navigateToLogin()
            return
        }

        setContent {
            ProyectoFinalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        onSignOut = { signOut() }
                    )
                }
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun signOut() {
        auth.signOut()
        navigateToLogin()
    }
}