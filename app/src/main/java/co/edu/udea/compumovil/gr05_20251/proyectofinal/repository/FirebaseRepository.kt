package co.edu.udea.compumovil.gr05_20251.proyectofinal.repository

// FirebaseRepository.kt
import co.edu.udea.compumovil.gr05_20251.proyectofinal.data.Actividad
import co.edu.udea.compumovil.gr05_20251.proyectofinal.data.RegistroActividad
import co.edu.udea.compumovil.gr05_20251.proyectofinal.data.Subactividad
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()

    // Obtener todas las actividades
    suspend fun obtenerActividades(): List<Actividad> {
        return try {
            val snapshot = db.collection("actividades")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Actividad::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Obtener subactividades por actividad ID
    suspend fun obtenerSubactividadesPorActividad(actividadId: String): List<Subactividad> {
        return try {
            val snapshot = db.collection("subactividades")
                .whereEqualTo("actividadId", actividadId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Subactividad::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Guardar registro de actividad
    suspend fun guardarRegistroActividad(registro: RegistroActividad): Result<String> {
        return try {
            val docRef = db.collection("registros_actividades")
                .add(registro)
                .await()

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}