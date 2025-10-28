package antonio.femxa.appfinal

//import android.R
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen


class MainActivity : AppCompatActivity() {
    //     var mediaPlayer: MediaPlayer? = null
//     var musicaOnOff: Boolean = false

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
        setContentView(R.layout.activity_inicial)


        retardo()
        animacionSalidaSplash()


        //ponerTexto()

       musicaOnOff = intent.getBooleanExtra("SonidoOn-Off", true)

        val botonSonido = findViewById<Button>(R.id.botonsonido)

        if (musicaOnOff) {
            SonidoGestion.iniciarMusica(this, R.raw.inicio1)
            botonSonido.text = "SONIDO OFF"
        } else {
            botonSonido.text = "SONIDO ON"
        }

        botonSonido.setOnClickListener {
            musicaOnOff = !SonidoGestion.musicaSonando()
            if (SonidoGestion.musicaSonando()) {
                SonidoGestion.pausarMusica()
                botonSonido.text = "SONIDO ON"
            } else {
                SonidoGestion.iniciarMusica(this, R.raw.inicio1)
                botonSonido.text = "SONIDO OFF"
            }
        }


        //acción botón hacia atrás
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
                SonidoGestion.detenerMusica()  // detengo musica
            }
        })
    }

    fun aJugar(v: View?) {
        val intent = Intent(
            this,
            CategoriaActivity::class.java
        )

//        if (musicaOnOff) {
//            intent.putExtra("SonidoOn-Off", true)
//        } else {
//            intent.putExtra("SonidoOn-Off", false)
//        }
        intent.putExtra("SonidoOn-Off", SonidoGestion.musicaSonando())

        startActivity(intent)
    }



    fun abrirCreditos(v: View?) {
        val intent = Intent(this, CreditosActivity::class.java)
        startActivity(intent)
    }


    override fun onPause() {
        super.onPause()
        // Si quieres que se pause la música al salir temporalmente de la app:
        // SonidoGestion.pausarMusica()
    }

    override fun onResume() {
        super.onResume()
        // Si estaba activa antes, reanuda
        if (musicaOnOff && !SonidoGestion.musicaSonando()) {
            SonidoGestion.iniciarMusica(this, R.raw.inicio1)
        }
    }


    /**
    Por defecto, cuando ya se ha dibujado la Actividad Principal, la Splash Screen
    desaparece. Sin emabargo, al programar esta función Predraw no se pinta ningún
    fotograma, hasta que no esta función no devuelta true. Por ejemplo
    en este caso, estamos causando un retardo de 6 segundos y hasta que no acabe
    la actividad no empieza a pintarse y mientras, se ve sólo la Splash Screen
     */
    fun retardo() {
        // Set up an OnPreDrawListener to the root view.
        //OJO android.R.id.content apunta al FrameLayout que contiene toda la interfaz de tu Activity.
        //Ese content existe siempre, todos nuestros layouts montan en este Frame y sigue estando en JetPack Compose
        val content = findViewById<View>(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Check whether the initial data is ready.
                    Thread.sleep(5000)
                    return if (true) {
                        // The content is ready. Start drawing.
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        // The content isn't ready. Suspend.
                        false
                    }
                }
            })
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
                    .setDuration(3000L)
                    .withEndAction {
                        splashScreenView.remove()
                    }
            }
        }
    }
}