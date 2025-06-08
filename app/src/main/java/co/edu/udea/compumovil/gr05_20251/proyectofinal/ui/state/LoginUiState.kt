package co.edu.udea.compumovil.gr05_20251.proyectofinal.ui.state

/**
 * UI State para la pantalla de login
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSignUpMode: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false
)