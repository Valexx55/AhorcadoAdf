package antonio.femxa.appfinal


import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlin.random.Random


class CategoriaActivity : AppCompatActivity() {
    private var intent: Intent? = null
    private var musicaOnOff: Boolean = true
    private lateinit var botonSonido: ImageButton
    private lateinit var recyclerCategorias: RecyclerView
    private var mapaFb : Map<String, List<String>>? = null
    var interstitialAd : InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_categoria)


        //mostrarAnuncio()
        // --- Recuperar estado de sonido desde SharedPreferences ---
        musicaOnOff = SonidoGestion.obtenerEstadoSonido(this)

        recyclerCategorias = findViewById(R.id.recycler_categorias)

        mapaFb = PalabrasRepository.getMapaPalabras()
        loadRecyclerCategorias()

        botonSonido = findViewById<ImageButton>(R.id.btnImagen)


        // --- Configurar botón de sonido ---
        actualizarIconoBotonSonido()

        botonSonido.setOnClickListener {
            val sonidoActivo = SonidoGestion.alternarSonido(this)
            musicaOnOff = sonidoActivo
            actualizarIconoBotonSonido()

            if (sonidoActivo) {
                SonidoGestion.iniciarMusica(this, R.raw.categoria)
            } else {
                SonidoGestion.pausarMusica()
            }
        }

        // --- Botón atrás ---
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@CategoriaActivity, MainActivity::class.java)
                intent.putExtra("SonidoOn-Off", SonidoGestion.musicaSonando())
                startActivity(intent)
                finish()
            }
        })

        // --- Iniciar música si estaba activa ---
        if (musicaOnOff && !SonidoGestion.musicaSonando()) {
            SonidoGestion.iniciarMusica(this, R.raw.categoria)
        }


    } // fin onCreate

    private fun mostrarAnuncio() {
        InterstitialAd.load(
            this,
            "ca-app-pub-9910445535228761/8258514024",
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d("MIAPP", "Ad was loaded.")
                    interstitialAd = ad

                    interstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("MIAPP", "Ad was dismissed.")
                                // Don't forget to set the ad reference to null so you
                                // don't show the ad a second time.
                                WindowCompat.setDecorFitsSystemWindows(window, false)
                                interstitialAd = null
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                // Called when fullscreen content failed to show.
                                Log.d("MIAPP", "Ad failed to show.")
                                // Don't forget to set the ad reference to null so you
                                // don't show the ad a second time.
                                interstitialAd = null
                            }

                            override fun onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                Log.d("MIAPP", "Ad showed fullscreen content.")
                            }

                            override fun onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                Log.d("MIAPP", "Ad recorded an impression.")
                            }

                            override fun onAdClicked() {
                                // Called when ad is clicked.
                                Log.d("MIAPP", "Ad was clicked.")
                            }
                        }
                    WindowCompat.setDecorFitsSystemWindows(window, true)
                    interstitialAd?.show(this@CategoriaActivity)
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("MIAPP", adError.message)
                    interstitialAd = null
                }
            },
        )
    }

    override fun onResume() {
        super.onResume()

        if (musicaOnOff && !SonidoGestion.musicaSonando()) {
            SonidoGestion.iniciarMusica(this, R.raw.categoria)
        }
    }

    override fun onPause() {
        super.onPause()

        SonidoGestion.pausarMusica()
    }

    /**
     * Cargamos el recycler con las palabras desde Firebase o el archivo xml, si Firebase no está disponible.
     * Configura el GridLayoutManager de 2 columnas para mostrar las categorías en 2 columnas
     */
    fun loadRecyclerCategorias() {
        var listaCategorias: List<String> = emptyList()

        // Cargamos las categorías de Firebase o el xml local
        mapaFb?.let { mapa ->
            listaCategorias = mapa.keys.toList()
        } ?: run {
            listaCategorias = resources.getStringArray(R.array.categorias).toList()
        }

        // GridLayout con 2 columnas
        recyclerCategorias.layoutManager = GridLayoutManager(this, 2)

        val adapter = CategoriasAdapter(listaCategorias) { categoria, position ->
            onCategoriaSelected(categoria, position)
        }

        recyclerCategorias.adapter = adapter
    }

    /**
     * Selección de la categoría.

     * @param categoria Nombre de la categoría seleccionada
     * @param position Posición de la categoría en el RecyclerView
     */
    fun onCategoriaSelected(categoria: String, position: Int) {
        var arrayPalabrasSeleccionado: Array<out CharSequence>? = null

        // Cargamos las categorías de Firebase o el xml local
        mapaFb?.let { mapa ->
            arrayPalabrasSeleccionado = mapa[categoria]?.toTypedArray()
        } ?: run {
            val arrayCategorias = resources.obtainTypedArray(R.array.array_categorias)
            arrayPalabrasSeleccionado = arrayCategorias.getTextArray(position)
            arrayCategorias.recycle()
        }

        val palabra = palabraOculta(arrayPalabrasSeleccionado)
        Log.d("MIAPP", palabra)

        intent = Intent(this@CategoriaActivity, TableroActivity::class.java)
        intent!!.putExtra("palabra_clave", palabra)
        intent!!.putExtra("categoria_seleccionada", categoria)

        SonidoGestion.detenerMusica()
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    /**
     * Dado un array de strings te devuelve un string aleatorio de ese array
     * @param array_especifico
     * @return
     */
    fun palabraOculta(array_especifico: Array<out CharSequence>?): String {
        var palabra: String? = null

            palabra = array_especifico?.random(Random(System.nanoTime())).toString()

        return palabra
    }

    private fun actualizarIconoBotonSonido() {
        if (musicaOnOff) {
            botonSonido.setImageResource(R.drawable.ic_volume_up)
        } else {
            botonSonido.setImageResource(R.drawable.ic_volume_off)
        }
    }

}