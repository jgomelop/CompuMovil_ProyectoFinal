package co.edu.udea.compumovil.gr05_20251.proyectofinal

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.udea.compumovil.gr05_20251.proyectofinal.sections.listaractividades.ListarActividadesScreen
import co.edu.udea.compumovil.gr05_20251.proyectofinal.sections.listaractividades.ListarActividadesViewModel
import co.edu.udea.compumovil.gr05_20251.proyectofinal.sections.registraractividad.RegistrarActividadScreen
import co.edu.udea.compumovil.gr05_20251.proyectofinal.sections.registraractividad.RegistrarActividadViewModel
import co.edu.udea.compumovil.gr05_20251.proyectofinal.ui.theme.ProyectoFinalTheme

data class DrawerItem(val title: String, val route: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val items = listOf(
        DrawerItem("Registrar Actividad", "registrarActividad"),
        DrawerItem("Listar Actividades", "listarActividades")
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                items.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.title) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(item.route)
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Actividades OAI") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "registrarActividad",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("registrarActividad") {
                    val viewModel: RegistrarActividadViewModel = viewModel()
                    RegistrarActividadScreen(viewModel)
                }
                composable("listarActividades") {
                    val viewModel: ListarActividadesViewModel = viewModel()
                    ListarActividadesScreen(viewModel)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AppNavigationPreview() {
    ProyectoFinalTheme {
        AppNavigation()
    }
}