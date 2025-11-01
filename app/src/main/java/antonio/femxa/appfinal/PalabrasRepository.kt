package antonio.femxa.appfinal

import android.util.Log
import antonio.femxa.appfinal.PalabrasRepository.mapaPalabras
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.util.SortedMap

object PalabrasRepository {


    //si tengo null, no se han podido cargar los datos de Firebase y habrá que tirar de local
    private var mapaPalabras: SortedMap<String, List<String>>? = null //murable y ordenado. por debajo es un treemap de java tal cual


    suspend fun preCargarPalabras ()
    {

        try {
            val db = Firebase.firestore
            val snapshot = db.collection("categorías").get().await()

            if (snapshot.isEmpty) { //no se ha podido leer de FB
                throw Exception("Sin palabras remotas")
            }

            else {
                mapaPalabras =  sortedMapOf<String, List<String>>()//mutableMapOf
                for (doc in snapshot.documents) { //recorremos las categorìas y añadimos sus valores a cada una

                    val nombreCategoria = doc.id
                    val valores = doc.get("valores") as? List<String> ?: emptyList()
                    mapaPalabras?.put(nombreCategoria, valores)

                }
                Log.d("MIAPP", "MAPA FB: $mapaPalabras")
                sustituirEnyePalabras()

            }


        } catch (e: Exception) {
            Log.e("Firestore", "Error leyendo categorias", e)
            throw  e
        }

    }

    /**
     * convertir texto que contiene códigos Unicode escapados
     * en los caracteres reales Unicode
     * esto \uD83C\uDDEA\uD83C\uDDF8 pasa a esto 🇪🇸
     */
    private fun decodeUnicodeEscapes(input: String): String {
        val regex = Regex("""\\u([0-9A-Fa-f]{4})""")
        return regex.replace(input) {
            val codePoint = it.groupValues[1].toInt(16)//de hexadecimal a entero
            codePoint.toChar().toString()//de entero a Unicode
        }
    }

    /**
     *OJO caso especial para incluir la bandera de españa en la categoría
     * //de eñe palabras. Hay un problema porque ANdroid usa UTF 16 y Firebase UTF 8
     * //Esto obliga a pasar el icono a Firebase, no el código, porque si no, no se
     * //interpreta. Y en esas, la bandera no existe como icono predefinidos, si no
     * //que se compila con el código Eñe palabras \uD83C\uDDEA\uD83C\uDDF8
     * //Entonces tenemos que sustituir esa clave al descargar el listado de FB
     */
    private fun sustituirEnyePalabras() {

        val valor = mapaPalabras?.get("Eñe palabras")

        if (valor != null) {
            mapaPalabras?.remove("Eñe palabras")
            val textoOriginal = "Eñe palabras \\uD83C\\uDDEA\\uD83C\\uDDF8"
            val textoDecodificado = decodeUnicodeEscapes(textoOriginal)
            mapaPalabras?.put(textoDecodificado, valor)
        }
    }

    fun getMapaPalabras(): Map<String, List<String>>? = mapaPalabras

}