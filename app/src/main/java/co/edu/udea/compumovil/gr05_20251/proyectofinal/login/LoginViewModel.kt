package co.edu.udea.compumovil.gr05_20251.proyectofinal.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    var email = mutableStateOf("")
        private set

    var password = mutableStateOf("")
        private set

    fun onEmailChange(newEmail: String) {
        email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password.value = newPassword
    }
}
