package co.edu.udea.compumovil.gr05_20251.proyectofinal.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = viewModel.email.value,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Correo electrónico") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.password.value,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Contraseña") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* Acción de login */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val fakeViewModel = remember { LoginViewModel() }
    LoginScreen(viewModel = fakeViewModel)
}
