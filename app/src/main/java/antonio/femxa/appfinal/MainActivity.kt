package antonio.femxa.appfinal

//import android.R
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private lateinit var botonSonido: Button
    private var musicaOnOff: Boolean = true


    /**
     * TODO SPLASHSCREEN - VALE
     * TODO CATEGORIAS - EXTERNALIZAR EN UN API GITHUB FIREBASE??/ AMPLIAR CATEGORÍAS Y PALABRAS

     * TODO PROBLEMA TECLADO DIMENSIÓN DE LAS LETRAS / TIPO DE FUENTE / TAMAÑO DE LAS TECLAS (JUAN MA)

     * TODO PALALBRA A RESOLVER NO SE VE EN EMULADOR - problema color tema oscuro

     * TODO PROBLEMA SONIDO INCONSISTENT y sigue sonando al salir de la app (PREFERENCES/CLASE GESTIÓN APARTE)

     * TODO CRÉDITOS REFORMULAR cambiar huevo de pascua
     * TODO revisar tHENNAS Y TEMA
     * TODO AÑADIR ALGUNA ANIMACIÓN
     * TODO AÑADIR ANUNCIOS
     * TODO REVISAR NAVEGACIÓN
     * TODO CATEGORIAS TAMAÑO SPINNER PARA QUE SE VEA BIEN TODO
     * TODO CAPITALIZACIÓN DERROTA VICTORIA ACTIVITY
     * TODO REVISAR MEJORA APP ICONO VISIBLIDAD e IMAGEN DE FONDO INICIAL
     * TODO eliminar referencias a femxa
     * TODO ACTUALIZAR VERSIONES DEL TARGETSDK Y LAS PROPIAS DE LA PUBLICACIÓN
     */


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Ahorcado)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicial)
        pruebaMuestraMapa()
        preCargaRetardo()
        animacionSalidaSplash()

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


    }  // Fin onCreate

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
        pruebaMuestraMapa()
        // Si estaba activa antes, reanuda
        if (musicaOnOff && !SonidoGestion.musicaSonando()) {
            SonidoGestion.iniciarMusica(this, R.raw.main)
        }
    }

    private fun actualizarTextoBotonSonido() {
        botonSonido.text = if (musicaOnOff) "SONIDO OFF" else "SONIDO ON"
    }

    /**
    Por defecto, cuando ya se ha dibujado la Actividad Principal, la Splash Screen
    desaparece. Sin emabargo, al programar esta función Predraw no se pinta ningún
    fotograma, hasta que no esta función no devuelta true. Por ejemplo
    en este caso, estamos causando un retardo de 6 segundos y hasta que no acabe
    la actividad no empieza a pintarse y mientras, se ve sólo la Splash Screen
     */
    fun preCargaRetardo() {
        // Set up an OnPreDrawListener to the root view.
        //OJO android.R.id.content apunta al FrameLayout que contiene toda la interfaz de tu Activity.
        //Ese content existe siempre, todos nuestros layouts montan en este Frame y sigue estando en JetPack Compose
        val content = findViewById<View>(android.R.id.content)
         var datosListos = false
         var cargaLanzada = false
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {

                    if (datosListos) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        return true // ahora sí, permitir dibujo
                    }

                    if (!cargaLanzada) {
                        cargaLanzada = true
                        lifecycleScope.launch {
                            try {
                                withContext(Dispatchers.IO) {
                                    PalabrasRepository.preCargarPalabras()
                                }
                                datosListos = true
                                content.invalidate() // fuerza nuevo preDraw
                            } catch (e: Exception) {
                                e.printStackTrace()
                                // Maneja error: podrías mostrar un mensaje o reintentar
                            }
                        }
                    }
                    return false // suspende el primer dibujo hasta que se completen los datos
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
        //sólo para versiones anteriores
        //también podría obtener la instancia con val splashScreen = installSplashScreen()
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener { splashScreenView ->
                // Create your custom animation.
                val slideUp = ObjectAnimator.ofFloat(
                    splashScreenView,
                    View.TRANSLATION_X,
                    0f,
                    -splashScreenView.width.toFloat()
                )
                slideUp.interpolator = AnticipateInterpolator()
                slideUp.duration = 20000L

                // Call SplashScreenView.remove at the end of your custom animation.
                slideUp.doOnEnd { splashScreenView.remove() }

                // Run your animation.
                slideUp.start()
            }
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener { splashScreenView ->
                splashScreenView.iconView!!.animate()
                    .alpha(0f)
                    .setDuration(900L)
                    .withEndAction {
                        splashScreenView.remove()
                    }
            }
        }
    }

}