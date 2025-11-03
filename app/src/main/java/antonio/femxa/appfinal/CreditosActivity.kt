package antonio.femxa.appfinal

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class CreditosActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_creditos)

        val container = findViewById<FrameLayout>(R.id.creditosContainer)
        val imagenFinal = findViewById<ImageView>(R.id.logoFinal)

        val lineas = getString(R.string.creditos).split("\n")
        val screenHeight = resources.displayMetrics.heightPixels

        initMediaPlayer(R.raw.superman)
        mostrarCreditos(container, lineas, screenHeight)
        animaLogo(imagenFinal, screenHeight, lineas)
        huevoPascua(imagenFinal)
    }

//    private fun initMediaPlayer() {
//        mediaPlayer = MediaPlayer.create(this, R.raw.soni).apply {
//            isLooping = false
//            setVolume(1.0f, 1.0f)
//        }
//    }

    private fun initMediaPlayer(musica: Int) {
        mediaPlayer = MediaPlayer.create(this, musica).apply {
            isLooping = false
            setVolume(1.0f, 1.0f)
        }
        mediaPlayer!!.start()
    }

    private fun mostrarCreditos(container: FrameLayout, lineas: List<String>, screenHeight: Int) {
        val baseStartY = screenHeight.toFloat()
        val spacingY = screenHeight.toFloat() / (lineas.size + 1)

        lineas.forEachIndexed { index, texto ->
            val startY = baseStartY + (index * spacingY)
            val endY = -200f

            val textView = crearLineaCredito(texto, startY)
            container.addView(textView)

            animarLineaCredito(textView, startY, endY, index, screenHeight)
        }
    }

    /**
     * Configura la apariencia de cada línea de crédito
     * @param texto línea de crédito a mostrar
     * @param posicionInicialEjeY la posición vertical inicial desde la que comenzará la animación (fuera de la pantalla).
     * @return se devuelve un nuevo TextView con la línea creada
     */
    private fun crearLineaCredito(texto: String, posicionInicialEjeY: Float): TextView {
        val textView = TextView(this)

        // Estilos base
        textView.setTextColor(Color.WHITE)
        textView.textSize = 20f
        textView.gravity = Gravity.CENTER
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        textView.setPadding(0, 20, 0, 20)
        textView.setSingleLine(false)
        textView.ellipsize = null
        textView.maxLines = Integer.MAX_VALUE
        textView.setLineSpacing(10f, 1f)
        textView.translationY = posicionInicialEjeY
        textView.visibility = View.INVISIBLE

        textView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            setMargins(0, 20, 0, 20)
        }

        // Aplicar estilos especiales si contiene nombre o email
        val spannable = SpannableString(texto)

        if (texto.contains("@") || texto.contains("github")) {
            // Estilo cursiva para correos
            textView.setTextColor(getColor(R.color.md_theme_outline))
            spannable.setSpan(
                android.text.style.StyleSpan(Typeface.ITALIC),
                0, texto.length,
                android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else if (texto.contains("EQUIPO VALEXX")) {
            // Estilo cursiva para cabecera
            textView.setTextColor(getColor(R.color.ic_launcher_background))
            textView.textSize = 28f
            spannable.setSpan(
                android.text.style.StyleSpan(Typeface.BOLD),
                0, texto.length,
                android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else if (texto.contains(":")) {
            // Estilo cursiva para apartados
            textView.setTextColor(Color.WHITE)
            spannable.setSpan(
                android.text.style.StyleSpan(Typeface.NORMAL),
                0, texto.length,
                android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            // Estilo negrita para nombres completos
            textView.setTextColor(Color.WHITE)
            spannable.setSpan(
                android.text.style.StyleSpan(Typeface.BOLD),
                0, texto.length,
                android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        textView.text = spannable
        return textView
    }

    /**
     * Anima una línea de texto (TextView) para que se desplace verticalmente de forma ascendiente.
     * Adicionalmente se aplica un efecto de escala para dar sensación de profundidad.
     * @param textView El TextView que contiene la línea de texto.
     * @param posicionInicialEjeY Posición vertical inicial (abajo, fuera de la pantalla).
     * @param posicionFinalEjeY Posición vertical inicial (arriba, fuera de la pantalla).
     * @param posicion Posición de la línea en la lista, usada para escalonar la animación.
     * @param alturaPantalla altura total de la pantalla, usada para calcular visibilidad y escala.
     */
    private fun animarLineaCredito(
        textView: TextView,
        posicionInicialEjeY: Float,
        posicionFinalEjeY: Float,
        posicion: Int,
        alturaPantalla: Int
    ) {
        ValueAnimator.ofFloat(posicionInicialEjeY, posicionFinalEjeY).apply {
            duration = 15000L // la animación dura 10 segundos.
            startDelay = posicion * 350L // cada línea espera un poco más que la anterior (500 ms por posicion) para crear un efecto escalonado.
            interpolator = LinearInterpolator() // para que el movimiento sea constante.

            addUpdateListener { animation -> // se actualiza la posición vertical en cada frame de la animación.
                val y = animation.animatedValue as Float
                textView.translationY = y

                if (textView.visibility == View.INVISIBLE && y < alturaPantalla - 100f) { // se hace visible el texto cuando está cerca de entrar en la pantalla (100 píxeles antes del borde inferior).
                    textView.visibility = View.VISIBLE
                }

                // Efecto de escala / profundidad
                val tramo = 1f - (y / alturaPantalla)  // Calcula un tramo relativo a la altura de la pantalla.
                val escala = 1f - (tramo * 0.7f) // Reduce el tamaño del texto conforme sube, simulando que se aleja (hasta un 70% de reducción).
                textView.scaleX = escala
                textView.scaleY = escala
            }

            start()
        }
    }

    /**
     * Genera la animación del logotipo (imagenFinal) para que aparezca al final de los créditos durante su desplazamiento.
     * @param imagenFinal el ImageView que contiene el logotipo.
     * @param screenHeight altura total de la pantalla del dispositivo
     * @param lineas lista de líneas de texto que forman los créditos.
     * @return
     */
    private fun animaLogo(imagenFinal: ImageView, screenHeight: Int, lineas: List<String>) {
        val baseInicioEjeY = screenHeight.toFloat() // punto de partida vertical para la primera línea de crédito (parte inferior de la pantalla).
        val espacioVertical = screenHeight.toFloat() / (lineas.size + 1) // espacio vertical entre cada línea de texto.
        val ultimaLineaInicioEjeY = baseInicioEjeY + ((lineas.size - 1) * espacioVertical) // posición inicial de la última línea de los créditos.
        val mitadPantalla = screenHeight / 2f // punto medio de la pantalla.
        val distanciaTotal = ultimaLineaInicioEjeY - (-200f) // cuánto se moverá la última línea desde su posición inicial hasta salir por arriba.
        val distanciaHastaMitad = ultimaLineaInicioEjeY - mitadPantalla // cuánto debe recorrer la última línea para llegar al centro de la pantalla.
        val tiempoHastaMitad = (lineas.size - 1) * 350L + ((distanciaHastaMitad / distanciaTotal) * 15000L).toLong() // Calcula el tiempo en milisegundos en el que la última línea estará en el centro de la pantalla.

        // Preparación del logotipo antes de la animación (se oculta, se hace transparente y se reduce a la mitad)
        imagenFinal.apply {
            visibility = View.INVISIBLE
            alpha = 0f
            scaleX = 0.5f
            scaleY = 0.5f
        }

        handler.postDelayed({ // se lanza una vez finaliza el tiempo que tarda en llegar la última línea de créditos a la mitad de la pantalla.
            imagenFinal.visibility = View.VISIBLE
            imagenFinal.post {
                val altoImagen = imagenFinal.height
                val centroEjeY = (screenHeight - altoImagen) / 2f

                imagenFinal.translationY = screenHeight.toFloat()

                imagenFinal.animate()
                    .translationY(centroEjeY)// lo mueve al centro vertical
                    .scaleX(1f) // lo escala a tamaño normal
                    .scaleY(1f)
                    .alpha(1f) // lo hace completamente visible
                    .setDuration(2000L) // duración de la animación: 2 segundos
                    .setInterpolator(DecelerateInterpolator(1.5f)) // animación suave con desaceleración
                    .start()
            }
        }, tiempoHastaMitad)
    }

    /**
     * Genera un huevo de Pascua, al hacer una pulsación larga en el logo.
     * @param logo: El logo de la aplicación.
     */
    private fun huevoPascua(logo: ImageView) {
        logo.setOnLongClickListener {
//            mediaPlayer!!.start()
            initMediaPlayer(R.raw.soni)
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }


}

