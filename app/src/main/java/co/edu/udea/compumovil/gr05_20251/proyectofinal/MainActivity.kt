package co.edu.udea.compumovil.gr05_20251.proyectofinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel

import co.edu.udea.compumovil.gr05_20251.proyectofinal.ui.theme.ProyectoFinalTheme
import kotlinx.coroutines.launch
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import co.edu.udea.compumovil.gr05_20251.proyectofinal.sections.listaractividades.ListarActividadesScreen
import co.edu.udea.compumovil.gr05_20251.proyectofinal.sections.listaractividades.ListarActividadesViewModel
import co.edu.udea.compumovil.gr05_20251.proyectofinal.sections.registraractividad.RegistrarActividadScreen
import co.edu.udea.compumovil.gr05_20251.proyectofinal.sections.registraractividad.RegistrarActividadViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoFinalTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val items = listOf(
        DrawerItem("Registrar Actividad", "registrar"),
        DrawerItem("Lista Actividades", "lista")
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
        NavHost(navController, startDestination = "registrar") {
            composable("registrar") {
                val viewModel: RegistrarActividadViewModel = viewModel()
                RegistrarActividadScreen(viewModel)
            }
            composable("lista") {
                val viewModel: ListarActividadesViewModel = viewModel()
                ListarActividadesScreen(viewModel)
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

data class DrawerItem(val title: String, val route: String)

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProyectoFinalTheme {
        Greeting("Android")
    }
}