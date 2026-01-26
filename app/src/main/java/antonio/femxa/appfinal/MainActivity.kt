package antonio.femxa.appfinal

//import android.R
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnticipateInterpolator
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.window.SplashScreenView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import antonio.femxa.appfinal.categorias.CategoriaActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.nio.charset.StandardCharsets


class MainActivity : AppCompatActivity() {

    private lateinit var botonSonido: LinearLayout
    private var musicaOnOff: Boolean = true
    private var cargaCompletada = false
    private var datosOk=false
    var interstitialAd : InterstitialAd? = null



    /**
     *
     *
     *
     * TODO AÑADIR ANUNCIOS ANUNCIO INSTERSTICIAL, GESTIONARLO PARA QUE NO SALGA SIEMPRE
     * Y QUE SE CUENTE CON UN PREFERENCES, QUE SALGA SÓLO DE VEZ EN CUANDO
     *
     * TODO REVISAR EL DISEÑO DE TODAS LAS PANTALLAS DESPUÉS INTEGRAR LOS ANUNCIOS
     *
     * TODO MEJORAR MASINFOACTIVITY ROBUSTEZ DE CONEXIÓN A INTERNET: CHEQUEAR SI HAY Y RED Y SI
     *
     * TODO PROBAR LAS PALABRAS MÁS LARGAS EN EL TABLERO
     *
     * TODO MEJORAR LA ALEATORIEDAD DE LAS PALABRAS
     * TODO MEJORAR LA GESTIÓN DE LOGS PARA QUE EN PRODUCCIÓN NO VAYA NADA
     *
     * no la forzar renovación e informar
     *
     ***comprobar habría que añadir ACCESS NETWOR STATE permisos
     * fun isNetworkAvailable(context: Context): Boolean {
     *     val connectivityManager =
     *         context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
     *     val network = connectivityManager.activeNetwork ?: return false
     *     val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
     *     return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
     * }
     *
     *
     * ****comprobar informar
     * if (isNetworkAvailable(this)) {
     *     webView.loadUrl("https://www.ejemplo.com")
     * } else {
     *     Toast.makeText(this, "Sin conexión a Internet", Toast.LENGTH_SHORT).show()
     * }
     *
     ******FORZAR
     * webView.clearCache(true)
     * webView.clearHistory()
     * webView.reload()
     *
     */


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Ahorcado)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicial)
        //pruebaMuestraMapa()

        preCargarDatos()
        retardoSplashScreen()
        animacionSalidaSplash()
        //TODO revisar posible incomaptilidad de corrutinas iniciailzando anuncios y cargando palabras de Firestore
        iniciarAnuncios()

        botonSonido = findViewById(R.id.botonsonido)

        // --- Recuperar estado de sonido desde SharedPreferences ---
        musicaOnOff = SonidoGestion.obtenerEstadoSonido(this)

        // --- Configurar estado inicial del botón ---
        actualizarTextoBotonSonido()

        // --- Iniciar música si está activada ---
        if (musicaOnOff && !SonidoGestion.musicaSonando()) {
            SonidoGestion.iniciarMusica(this, R.raw.main)
        }

        // --- Toggle del sonido al pulsar el botón ---
        botonSonido.setOnClickListener {
            val sonidoActivo = SonidoGestion.alternarSonido(this)
            musicaOnOff = sonidoActivo
            actualizarTextoBotonSonido()

            if (sonidoActivo) {
                SonidoGestion.iniciarMusica(this, R.raw.main)
            } else {
                SonidoGestion.pausarMusica()
            }
        }

        // --- Botón Atrás: cerrar app y detener música ---
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                SonidoGestion.detenerMusica()
                finishAffinity()
            }
        })

       // recopiaCategorias()

    }  // Fin onCreate


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
                    interstitialAd?.show(this@MainActivity)
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("MIAPP", adError.message)
                    interstitialAd = null
                }
            },
        )
    }

    private fun iniciarAnuncios() {
        //NOTA: Mejor usar el contexto de la app y no me
           lifecycleScope.launch(Dispatchers.IO) {
               MobileAds.initialize(applicationContext)
               {
                       initializationStatus: InitializationStatus ->
                   Log.d("MIAPP", "Anuncios Inicializados")
                    //mostrarAnuncio()
               }
           }

    }


    private fun pruebaMuestraMapa() {
       Log.d("MIAPP", "DATOS MAPA =  ${PalabrasRepository.getMapaPalabras()} ")
    }

    fun aJugar(v: View?) {
        val intent = Intent(
            this,
            CategoriaActivity::class.java
        )

        //intent.putExtra("SonidoOn-Off", SonidoGestion.musicaSonando())
        SonidoGestion.detenerMusica()
        startActivity(intent)
    }

    fun abrirCreditos(v: View?) {
        val intent = Intent(this, CreditosActivity::class.java)
        startActivity(intent)
    }

    override fun onPause() {
        super.onPause()
        SonidoGestion.pausarMusica()
    }

    override fun onResume() {
        super.onResume()
        // Si estaba activa antes, reanuda
        if (musicaOnOff && !SonidoGestion.musicaSonando()) {
            SonidoGestion.iniciarMusica(this, R.raw.main)
        }
       // iniciarAnuncios()
    }

    private fun actualizarTextoBotonSonido() {
       // botonSonido.text = if (musicaOnOff) "SONIDO OFF" else "SONIDO ON"
        val textView = botonSonido.getChildAt(1) as TextView   // El segundo hijo (posición 1)
        textView.text = if (musicaOnOff) "SONIDO OFF" else "SONIDO ON"
    }

    /**
    Por defecto, cuando ya se ha dibujado la Actividad Principal, la Splash Screen
    desaparece. Sin emabargo, al programar esta función Predraw no se pinta ningún
    fotograma, hasta que no esta función no devuelta true. Por ejemplo
    en este caso, estamos causando un retardo de 6 segundos y hasta que no acabe
    la actividad no empieza a pintarse y mientras, se ve sólo la Splash Screen
     */
    fun retardoSplashScreen() {
        // Set up an OnPreDrawListener to the root view.
        //OJO android.R.id.content apunta al FrameLayout que contiene toda la interfaz de tu Activity.
        //Ese content existe siempre, todos nuestros layouts montan en este Frame y sigue estando en JetPack Compose
        val content = findViewById<View>(android.R.id.content)

        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                //Android llama a onPreDraw() una vez por frame (≈ 60 veces por segundo, si hay algo que actualizar).
                override fun onPreDraw(): Boolean {

                    if (cargaCompletada) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                    }
                    Log.d("MIAPP", "Datos listos = $cargaCompletada")
                    return cargaCompletada

                }
            }
        )
    }


    /**
     * La salidad de la SplashScreen, puede ser animada. De modo, que podemos
     * definir un listener al finalizar su tiempo y cargar una animación
     * como ésta
     */
    fun animacionSalidaSplash() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener(::animacionHorizontal)
        }
/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener (::animacionIconoDegradado)
        }*/
    }

    @RequiresApi(Build.VERSION_CODES.S)
    //@TargetApi(Build.VERSION_CODES.S)
    fun animacionHorizontal (splashScreenView: SplashScreenView)
    {
        // Create your custom animation.
        val slideUp = ObjectAnimator.ofFloat(
            splashScreenView,
            View.TRANSLATION_X,
            0f,
            -splashScreenView.width.toFloat()
        )
        slideUp.interpolator = AnticipateInterpolator()
        slideUp.duration = 900L

        // Call SplashScreenView.remove at the end of your custom animation.
        slideUp.doOnEnd { splashScreenView.remove() }

        // Run your animation.
        slideUp.start()
    }


    @RequiresApi(Build.VERSION_CODES.S)
    fun animacionIconoDegradado (splashScreenView: SplashScreenView)
    {
        splashScreenView.iconView!!.animate()
            .alpha(0f)
            .setDuration(900L)
            .withEndAction {
                splashScreenView.remove()
            }
    }

    fun preCargarDatos ()
    {
        //Es un hilo independiente, no estoy en la Interfaz de Usuario
        lifecycleScope.launch(context = Dispatchers.IO) {



                try {
                    PalabrasRepository.preCargarPalabras()
                    datosOk = true
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("MIAPP", "ERROR al acceder a los datos ${e.message}")
                    datosOk = false
                } finally {
                    cargaCompletada = true

                }
            }
    }


    private fun recopiaCategorias() {
        lifecycleScope.launch {

            val arrayCategoriasViejo = arrayOf("animales","deportes", "eñe palabras", "estilos musicales", "famosos", "frutas", "internet", "lugares", "peliculas" )
            val arrayCategoriasNuevo = arrayOf("Animales 🦊","Deportes 🏅", "Eñe palabras", "Estilos musicales 🎵", "Famosos 🌟", "Frutas 🍎", "Internet 🌐", "Lugares 🗺️", "Películas 🎬" )

            if (arrayCategoriasNuevo.size == arrayCategoriasViejo.size)
            {
                arrayCategoriasViejo.forEachIndexed { index, categoriaVieja ->

                    renombrarDocumento("categorías", categoriaVieja, arrayCategoriasNuevo[index])

                }
            }


        }

    }

    suspend fun renombrarDocumento(
        nombreColeccion: String,
        idAntiguo: String,
        idNuevo: String
    ) {
        val db = FirebaseFirestore.getInstance()

        try {
            // 1️⃣ Obtener referencia de ambos documentos
            val oldDocRef = db.collection(nombreColeccion).document(idAntiguo)
            val newDocRef = db.collection(nombreColeccion).document(idNuevo)

            // 2️⃣ Leer los datos del documento antiguo
            val snapshot = oldDocRef.get().await()

            if (!snapshot.exists()) {
                Log.w("Firestore", "⚠️ El documento '$idAntiguo' no existe.")
                return
            }

            // 3️⃣ Copiar datos al nuevo documento
            val data = snapshot.data
            if (data != null) {
                newDocRef.set(data).await()
                Log.d("Firestore", "✅ Copiado a '$idNuevo'")
            }

            // 4️⃣ Borrar el documento antiguo
            oldDocRef.delete().await()
            //Log.d("Firestore", "🗑️ Borrado '$idAntiguo'")

            Log.d("Firestore", "✅ Documento renombrado correctamente de '$idAntiguo' a '$idNuevo'")

        } catch (e: Exception) {
            Log.e("Firestore", "❌ Error al renombrar documento: ${e.message}", e)
        }
    }



}