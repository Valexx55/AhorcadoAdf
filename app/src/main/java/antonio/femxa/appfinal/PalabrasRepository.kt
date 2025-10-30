package antonio.femxa.appfinal

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

object PalabrasRepository {


    private var mapaPalabras: MutableMap<String, List<String>>? = null

    suspend fun preCargarPalabras ()
    {

        try {
            val db = Firebase.firestore
            val snapshot = db.collection("categorías").get().await()


            if (snapshot.isEmpty) {
                throw Exception("Sin palabras remotas")
            }

            else {
                mapaPalabras = mutableMapOf<String, List<String>>()
                for (doc in snapshot.documents) { //recorremos las categorìas y añadimos sus valores a cada una

                    val nombreCategoria = doc.id
                    val valores = doc.get("valores") as? List<String> ?: emptyList()
                    mapaPalabras?.put(nombreCategoria, valores)

                }
                Log.d("MIAPP", "MAPA FB: $mapaPalabras")

            }


        } catch (e: Exception) {
            Log.e("Firestore", "Error leyendo categorias", e)
            throw  e
        }

    }

    fun getMapaPalabras(): Map<String, List<String>>? = mapaPalabras

}