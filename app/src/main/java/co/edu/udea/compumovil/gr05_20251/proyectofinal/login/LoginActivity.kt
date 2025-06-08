package co.edu.udea.compumovil.gr05_20251.proyectofinal.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.udea.compumovil.gr05_20251.proyectofinal.MainActivity
import co.edu.udea.compumovil.gr05_20251.proyectofinal.ui.theme.ProyectoFinalTheme

class LoginActivity : ComponentActivity() {
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ProyectoFinalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    // Si el usuario está autenticado, navegar a la pantalla principal
                    if (uiState.isLoggedIn) {
                        navigateToMainActivity()
                        return@Surface
                    }

                    LoginScreen(
                        uiState = uiState,
                        onEmailChanged = viewModel::updateEmail,
                        onPasswordChanged = viewModel::updatePassword,
                        onLoginClick = viewModel::login,
                        onSignUpClick = viewModel::signUp,
                        onToggleAuthMode = viewModel::toggleAuthMode
                    )
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}