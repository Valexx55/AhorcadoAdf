package antonio.femxa.appfinal

//import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen


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
        setContentView(R.layout.activity_inicial)


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

    fun aJugar(v: View?) {
        val intent = Intent(
            this,
            CategoriaActivity::class.java
        )

        //intent.putExtra("SonidoOn-Off", SonidoGestion.musicaSonando())
        SonidoGestion.detenerMusica()
        startActivity(intent)
    }

//    fun ponerTexto() {
//        val v1 = findViewById<View>(R.id.botonsonido)
//        val ib = v1 as Button
//
//        val v2 = findViewById<View>(R.id.botoncreditos)
//        val ib2 = v2 as Button
//
//        ib.text = "SONIDO OFF"
//        ib2.text = "CREDITOS"
//    }

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

//    override fun onStop() {
//        super.onStop()
//        // Si estaba activa antes, reanuda
//        if (SonidoGestion.musicaSonando()) {
//            SonidoGestion.detenerMusica()
//        }
//    }

}