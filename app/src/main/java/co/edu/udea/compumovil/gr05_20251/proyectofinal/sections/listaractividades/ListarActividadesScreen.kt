package co.edu.udea.compumovil.gr05_20251.proyectofinal.sections.listaractividades

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListarActividadesScreen(
    viewModel: ListarActividadesViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val showDialog by viewModel.showDialog.collectAsState()
    val selectedRegistro by viewModel.selectedRegistro.collectAsState()

    // Obtener el usuario actual de Firebase Auth
    val currentUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { userId ->
            viewModel.cargarRegistros(userId)
        }
    }

    // Si no hay usuario autenticado, mostrar mensaje
    if (currentUser == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = "Usuario no autenticado",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Registros de Actividades",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when (uiState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = (uiState as UiState.Error).message,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            is UiState.Success -> {
                if ((uiState as UiState.Success).registros.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No hay registros de actividades",
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Encabezado de la tabla
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                            )
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Actividad",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(2f),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Tiempo",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Fecha",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // Lista de registros
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                            )
                    ) {
                        items((uiState as UiState.Success).registros) { registro ->
                            RegistroRow(
                                registro = registro,
                                viewModel = viewModel,
                                onClick = { viewModel.mostrarDetalles(registro) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog para mostrar detalles
    if (showDialog && selectedRegistro != null) {
        DetalleRegistroDialog(
            registro = selectedRegistro!!,
            viewModel = viewModel,
            onDismiss = { viewModel.ocultarDetalles() }
        )
    }
}

@Composable
fun RegistroRow(
    registro: RegistroConDetalles,
    viewModel: ListarActividadesViewModel,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp)
            .background(
                if (registro.registro.comentarios.isNotEmpty())
                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                else
                    Color.Transparent
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Columna de actividad
        Column(
            modifier = Modifier.weight(2f)
        ) {
            Text(
                text = registro.nombreActividad,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (registro.nombreSubactividad != null) {
                Text(
                    text = registro.nombreSubactividad,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Tiempo
        Text(
            text = viewModel.formatearTiempo(
                registro.registro.horas,
                registro.registro.minutos
            ),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )

        // Fecha
        Text(
            text = viewModel.formatearFecha(registro.registro.fecha),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }

    Divider(
        modifier = Modifier.padding(horizontal = 12.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    )
}

@Composable
fun DetalleRegistroDialog(
    registro: RegistroConDetalles,
    viewModel: ListarActividadesViewModel,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header con botón de cerrar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Detalles del Registro",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Información del registro
                DetalleItem(
                    label = "Actividad:",
                    value = registro.nombreActividad
                )

                if (registro.nombreSubactividad != null) {
                    DetalleItem(
                        label = "Subactividad:",
                        value = registro.nombreSubactividad
                    )
                }

                DetalleItem(
                    label = "Fecha:",
                    value = viewModel.formatearFecha(registro.registro.fecha)
                )

                DetalleItem(
                    label = "Tiempo:",
                    value = viewModel.formatearTiempo(
                        registro.registro.horas,
                        registro.registro.minutos
                    )
                )

                if (registro.registro.comentarios.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Comentarios:",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = registro.registro.comentarios,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de cerrar
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}

@Composable
fun DetalleItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.End
        )
    }
}