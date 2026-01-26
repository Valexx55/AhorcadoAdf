package antonio.femxa.appfinal.categorias

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import antonio.femxa.appfinal.MainActivity
import antonio.femxa.appfinal.PalabrasRepository
import antonio.femxa.appfinal.R
import antonio.femxa.appfinal.SonidoGestion
import antonio.femxa.appfinal.TableroActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlin.random.Random

class CategoriaActivity : AppCompatActivity() {
    private lateinit var rvCategorias: RecyclerView
    private var intent: Intent? = null
    private var musicaOnOff: Boolean = true
    private lateinit var botonSonido: ImageButton
    private var mapaFb: Map<String, List<String>>? = null
    var interstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_categoria)


        //mostrarAnuncio()
        // --- Recuperar estado de sonido desde SharedPreferences ---
        musicaOnOff = SonidoGestion.obtenerEstadoSonido(this)

        mapaFb = PalabrasRepository.getMapaPalabras()
        rvCategorias = findViewById(R.id.rvCategorias)
        val categorias = obtenerCategorias()

        rvCategorias.layoutManager = GridLayoutManager(this, 2)

        rvCategorias.adapter = CategoriaAdapter(categorias) {

            lanzarJuego(it)

            Log.d("MIAPP", "Categoría elegida: ${it}")
        }

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

    /**
     * Cada vez que el activity vuelva de una pausa el spinner se coloca en la posicion selecciona una categoria
     */
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

    private fun obtenerCategorias(): List<String> {

        return this.mapaFb?.keys?.toList()
            ?: resources.getStringArray(R.array.categorias).toList()
    }

    private fun lanzarJuego(categoria: String) {

        // Si tengo los datos de Firebase, obtengo el array de palabras para la categoría seleccionada.
        val arrayPalabrasSeleccionado: Array<String> = mapaFb?.get(categoria)?.toTypedArray()
            ?: run {

                // Si no tengo los datos de Firebase, obtenemos los datos a partir del array local.
                val arrayCategorias = resources.obtainTypedArray(R.array.array_categorias)
                // Como la categoría seleccionada tiene un emoji,
                // y las posiciones de la lista con emojis y el array sin emojis coinciden,
                // obtenemos la posición de la categoría en la lista de las categorías con emojis.
                val listaCategoriasEmojis = obtenerCategorias()
                val posicionCategoria = listaCategoriasEmojis.indexOf(categoria)
                val palabras = arrayCategorias.getTextArray(posicionCategoria)
                arrayCategorias.recycle()
                palabras.map { it.toString() }.toTypedArray()
            }

        val palabra = palabraOculta(arrayPalabrasSeleccionado)

        val intent = Intent(this, TableroActivity::class.java)
        intent.putExtra("palabra_clave", palabra)
        intent.putExtra("categoria_seleccionada", categoria)

        SonidoGestion.detenerMusica()

        startActivity(
            intent,
            ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
        )
    }
}