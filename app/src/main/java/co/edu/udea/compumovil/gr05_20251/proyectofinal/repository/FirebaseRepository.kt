package co.edu.udea.compumovil.gr05_20251.proyectofinal.repository

// FirebaseRepository.kt
import co.edu.udea.compumovil.gr05_20251.proyectofinal.data.Actividad
import co.edu.udea.compumovil.gr05_20251.proyectofinal.data.RegistroActividad
import co.edu.udea.compumovil.gr05_20251.proyectofinal.data.Subactividad
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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

    // Guardar nuevo registro de actividad
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

    // Obtener todos los registros de actividades para un usuario
    suspend fun obtenerRegistrosActividades(userId: String): List<Pair<String, RegistroActividad>> {
        return try {
            val snapshot = db.collection("registros_actividades")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(RegistroActividad::class.java)?.let { registro ->
                    Pair(doc.id, registro)
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Obtener un solo registro de actividad por ID (PARA EDICIÓN)
    suspend fun obtenerRegistroActividadPorId(registroId: String): Pair<String, RegistroActividad>? {
        return try {
            val docRef = db.collection("registros_actividades").document(registroId)
            val snapshot = docRef.get().await()

            snapshot.toObject(RegistroActividad::class.java)?.let { registro ->
                Pair(snapshot.id, registro)
            }
        } catch (e: Exception) {
            null
        }
    }

    // Actualizar un registro de actividad existente
    suspend fun actualizarRegistroActividad(registroId: String, updatedRegistro: RegistroActividad): Result<Unit> {
        return try {
            db.collection("registros_actividades")
                .document(registroId)
                .set(updatedRegistro) // Using set to overwrite the document
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // Obtener una actividad por ID
    suspend fun obtenerActividadPorId(actividadId: String): Actividad? {
        return try {
            val snapshot = db.collection("actividades")
                .document(actividadId)
                .get()
                .await()

            snapshot.toObject(Actividad::class.java)?.copy(id = snapshot.id)
        } catch (e: Exception) {
            null
        }
    }

    // Obtener una subactividad por ID
    suspend fun obtenerSubactividadPorId(subactividadId: String): Subactividad? {
        return try {
            val snapshot = db.collection("subactividades")
                .document(subactividadId)
                .get()
                .await()

            snapshot.toObject(Subactividad::class.java)?.copy(id = snapshot.id)
        } catch (e: Exception) {
            null
        }
    }

    // Eliminar registro de actividad
    suspend fun eliminarRegistroActividad(registroId: String): Result<Unit> {
        return try {
            db.collection("registros_actividades")
                .document(registroId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
