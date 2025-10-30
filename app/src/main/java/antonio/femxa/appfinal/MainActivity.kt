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
import android.window.SplashScreenView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var botonSonido: Button
    private var musicaOnOff: Boolean = true
    private var cargaCompletada = false
    private var datosOk=false


    /**

     * TODO PALALBRA A RESOLVER NO SE VE EN EMULADOR - problema color tema oscuro
     * TODO revisar tHENNAS Y TEMA
     * TODO CRÉDITOS REFORMULAR cambiar huevo de pascua
     * TODO REVISAR DISEÑO SPINNER PARA QUE SE VEA BIEN TODO
     * TODO AÑADIR ALGUNA ANIMACIÓN
     * TODO AÑADIR ANUNCIOS
     * TODO CAPITALIZACIÓN DERROTA VICTORIA ACTIVITY
     * TODO REVISAR MEJORA APP ICONO VISIBLIDAD e IMAGEN DE FONDO INICIAL
     * TODO eliminar referencias a femxa
     * TODO homogeneizar el tamaño de la fuente en los botones de inicio
     * TODO integrar la publicidad
     * TODO REVISAR EL DISEÑO DE TODAS LAS PANTALLAS después integrar la publicidad
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

}