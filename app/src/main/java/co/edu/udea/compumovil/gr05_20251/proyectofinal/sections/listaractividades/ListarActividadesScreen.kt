package co.edu.udea.compumovil.gr05_20251.proyectofinal.sections.listaractividades

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.firebase.auth.FirebaseAuth
import co.edu.udea.compumovil.gr05_20251.proyectofinal.sections.registraractividad.RegistrarActividadActivity // Import RegistrarActividadActivity
import co.edu.udea.compumovil.gr05_20251.proyectofinal.ui.theme.GreenColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListarActividadesScreen(
    viewModel: ListarActividadesViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val showDialog by viewModel.showDialog.collectAsState()
    val showDeleteConfirmation by viewModel.showDeleteConfirmation.collectAsState()
    val selectedRegistro by viewModel.selectedRegistro.collectAsState()
    val isDeleting by viewModel.isDeleting.collectAsState()
    val deleteMessage by viewModel.deleteMessage.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current // Get the current lifecycle owner

    val currentUser = FirebaseAuth.getInstance().currentUser

    DisposableEffect(lifecycleOwner, currentUser?.uid) { // Added currentUser?.uid to the key for re-triggering if user changes
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                currentUser?.uid?.let { userId ->
                    viewModel.cargarRegistros(userId)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { // This onDispose is correctly used within DisposableEffect
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(deleteMessage) {
        deleteMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.limpiarMensajeBorrar()
        }
    }

    val currentRegistros = remember(uiState) {
        when (uiState) {
            is UiState.Success -> (uiState as UiState.Success).registros
            else -> emptyList() // Return empty list for Loading or Error states to prevent ClassCastException
        }
    }

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
            modifier = Modifier.padding(bottom = 16.dp),
            color = Color.DarkGray
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
                if (currentRegistros.isEmpty()) {
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
                                Color(0xFF8DC63F),
                                RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                            )
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Actividad",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(2f),
                            color = Color.White
                        )
                        Text(
                            text = "Tiempo",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                        Text(
                            text = "Fecha",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            color = Color.White
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
                        // Pass currentRegistros directly, no cast needed here
                        items(currentRegistros) { registro ->
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
            onDismiss = { viewModel.ocultarDetalles() },
            onDelete = { viewModel.mostrarConfirmacionBorrar() },
            onEdit = {
                // Navigate to RegistrarActividadActivity with the selected registroId
                val intent = Intent(context, RegistrarActividadActivity::class.java).apply {
                    putExtra(RegistrarActividadActivity.EXTRA_REGISTRO_ID, selectedRegistro!!.registroId)
                }
                context.startActivity(intent)
                viewModel.ocultarDetalles() // Close the details dialog
            }
        )
    }

    // Dialog de confirmación para eliminar
    if (showDeleteConfirmation && selectedRegistro != null) {
        ConfirmDeleteDialog(
            registro = selectedRegistro!!,
            isDeleting = isDeleting,
            onConfirm = {
                currentUser.uid?.let { userId ->
                    viewModel.borrarRegistro(userId)
                }
            },
            onDismiss = { viewModel.ocultarConfirmacionBorrar() }
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
            .background(Color.Transparent),
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
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
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
                        color = Color.DarkGray
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actividad
                Text(
                    text = "Actividad:",
                    fontWeight = FontWeight.Bold,
                    color = GreenColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = registro.nombreActividad,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Subactividad (si existe)
                if (registro.nombreSubactividad != null) {
                    Text(
                        text = "Subactividad:",
                        fontWeight = FontWeight.Bold,
                        color = GreenColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = registro.nombreSubactividad,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Fecha
                Text(
                    text = "Fecha:",
                    fontWeight = FontWeight.Bold,
                    color = GreenColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = viewModel.formatearFecha(registro.registro.fecha),
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tiempo
                Text(
                    text = "Tiempo:",
                    fontWeight = FontWeight.Bold,
                    color = GreenColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = viewModel.formatearTiempo(
                            registro.registro.horas,
                            registro.registro.minutos
                        ),
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Comentarios (sin modificar)
                if (registro.registro.comentarios.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Comentarios:",
                        fontWeight = FontWeight.Bold,
                        color = GreenColor
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

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Borrar",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Borrar")
                    }

                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = GreenColor
                        ),
                        border = BorderStroke(1.dp, GreenColor)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Editar")
                    }
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
            color = GreenColor,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun ConfirmDeleteDialog(
    registro: RegistroConDetalles,
    isDeleting: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isDeleting) onDismiss() },
        title = {
            Text(
                text = "Confirmar eliminación",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text("¿Estás seguro de que quieres eliminar este registro?")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Actividad: ${registro.nombreActividad}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                if (registro.nombreSubactividad != null) {
                    Text(
                        text = "Subactividad: ${registro.nombreSubactividad}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Esta acción no se puede deshacer.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isDeleting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onError
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(if (isDeleting) "Eliminando..." else "Eliminar")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                enabled = !isDeleting
            ) {
                Text("Cancelar")
            }
        }
    )
}
