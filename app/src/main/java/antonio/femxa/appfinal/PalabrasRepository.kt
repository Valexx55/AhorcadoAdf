package antonio.femxa.appfinal

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

object PalabrasRepository {


    private var mapaPalabras: MutableMap<String, List<String>>? = null

    suspend fun preCargarPalabras ()
    {

        try {
            val db = Firebase.firestore
            val snapshot = db.collection("categorías").get().await()
            mapaPalabras = mutableMapOf<String, List<String>>()

            for (doc in snapshot.documents) {

                val nombreCategoria = doc.id
                val valores = doc.get("valores") as? List<String> ?: emptyList()
                mapaPalabras?.put(nombreCategoria, valores)
                Log.d("MIAPP", "Categoría: $nombreCategoria → $valores")
                Log.d("MIAPP", "MAPA: $mapaPalabras")
            }

        } catch (e: Exception) {
            Log.e("Firestore", "Error leyendo categorias", e)
            throw  e
        }

    }

    fun getMapaPalabras(): Map<String, List<String>>? = mapaPalabras

}