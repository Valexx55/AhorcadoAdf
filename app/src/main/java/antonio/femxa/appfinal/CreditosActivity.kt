package antonio.femxa.appfinal

//import android.R
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


//class CreditosActivity : AppCompatActivity() {
//    var lv: ListView? = null
//    var mediaPlayer: MediaPlayer? = null
//    var clickCount = 0
//    val clickResetDelay = 2000L // 2 segundos
//    val handler = Handler(Looper.getMainLooper())
//    var resetRunnable: Runnable? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_creditos)
//
//        mediaPlayer = MediaPlayer.create(this, R.raw.soni)
//        mediaPlayer!!.isLooping = false
//        mediaPlayer!!.setVolume(100f, 100f)
//
//
//
//
//
//
//        val container = findViewById<FrameLayout>(R.id.creditosContainer)
//        val lineas = getString(R.string.creditos).split("\n")
//        val screenHeight = resources.displayMetrics.heightPixels
//        val baseStartY = screenHeight.toFloat()
//        val spacingY = screenHeight.toFloat() / (lineas.size + 1)
//
//        lineas.forEachIndexed { index, texto ->
//            val startY = baseStartY + (index * spacingY)
//            val endY = -200f
//
//            val textView = TextView(this).apply {
//                text = texto
//                setTextColor(Color.WHITE)
//                textSize = 20f
//                gravity = Gravity.CENTER
//                textAlignment = View.TEXT_ALIGNMENT_CENTER
//                setTypeface(null, Typeface.BOLD)
//                setPadding(0, 20, 0, 20)
//
//                // ✅ Permitir múltiples líneas sin corte
//                setSingleLine(false)
//                ellipsize = null
//                maxLines = Integer.MAX_VALUE
//
//                layoutParams = FrameLayout.LayoutParams(
//                    FrameLayout.LayoutParams.MATCH_PARENT,
//                    FrameLayout.LayoutParams.WRAP_CONTENT
//                ).apply {
//                    gravity = Gravity.CENTER_HORIZONTAL
//                    setMargins(0, 20, 0, 20)
//                }
//            }
//
//            textView.visibility = View.INVISIBLE
//            textView.setLineSpacing(10f, 1f)
//            textView.translationY = startY
//
//            container.addView(textView)
//
//
//            val animator = ValueAnimator.ofFloat(startY, endY).apply {
//                duration = 30000L
//                startDelay = index * 500L
//                interpolator = LinearInterpolator()
//
//                addUpdateListener { animation ->
//                    val y = animation.animatedValue as Float
//                    textView.translationY = y
//
//                    if (textView.visibility == View.INVISIBLE && y < screenHeight - 100f) {
//                        textView.visibility = View.VISIBLE
//                    }
//
//                    val progress = 1f - (y / screenHeight)
//                    val scale = 1f - (progress * 0.7f)
//                    textView.scaleX = scale
//                    textView.scaleY = scale
//                }
//            }
//
//            animator.start()
//        }
//
//        val imagenFinal = findViewById<ImageView>(R.id.logoFinal)
//
//
////        imagenFinal.setOnClickListener {
////            clickCount++
////
////            // Cancelar cualquier reset anterior
////            resetRunnable?.let { handler.removeCallbacks(it) }
////
////            // Programar reset del contador
////            resetRunnable = Runnable {
////                clickCount = 0
////            }
////            handler.postDelayed(resetRunnable!!, clickResetDelay)
////
////            // Si se superan los 3 clics rápidos, reproducir el sonido
////            if (clickCount >= 3) {
////                clickCount = 0 // reset inmediato
////                mediaPlayer?.start()
////            }
////        }
//
//        imagenFinal.setOnLongClickListener {
//            mediaPlayer!!.start()
//            true
//        }
//
//
//// Calcular cuándo la última línea estará a media pantalla
//        // La última línea empieza en: baseStartY + ((lineas.size - 1) * spacingY)
//        // Y se mueve desde ahí hasta -200f en 30000ms
//        // Queremos saber cuándo llega a screenHeight / 2
//
//        val ultimaLineaStartY = baseStartY + ((lineas.size - 1) * spacingY)
//        val mitadPantalla = screenHeight / 2f
//        val distanciaTotal = ultimaLineaStartY - (-200f)
//        val distanciaHastaMitad = ultimaLineaStartY - mitadPantalla
//
//        // Calcular el tiempo proporcional
//        val tiempoHastaMitad = (lineas.size - 1) * 500L + ((distanciaHastaMitad / distanciaTotal) * 30000L).toLong()
//
//        // Configurar estado inicial de la imagen
//        imagenFinal.visibility = View.INVISIBLE
//        imagenFinal.alpha = 0f
//        imagenFinal.scaleX = 0.5f
//        imagenFinal.scaleY = 0.5f
//
//        // Esperar a que la última línea esté a media pantalla
//        Handler(Looper.getMainLooper()).postDelayed({
//            Log.d("Creditos", "Iniciando animación del logo (última línea a media pantalla)")
//
//            // Hacer visible la imagen y medir su tamaño
//            imagenFinal.visibility = View.VISIBLE
//
//            imagenFinal.post {
//                // Calcular la posición Y del centro de la pantalla
//                val altoImagen = imagenFinal.height
//                val centroY = (screenHeight - altoImagen) / 2f
//
//                Log.d("Creditos", "Alto imagen: $altoImagen, Centro Y: $centroY, Screen height: $screenHeight")
//
//                // Posicionar la imagen completamente debajo de la pantalla
//                imagenFinal.translationY = screenHeight.toFloat()
//
//                // Animar el logo desde abajo hacia el centro
//                imagenFinal.animate()
//                    .translationY(centroY) // Mover al centro vertical
//                    .scaleX(1f)
//                    .scaleY(1f)
//                    .alpha(1f)
//                    .setDuration(2000L)
//                    .setInterpolator(DecelerateInterpolator(1.5f))
//                    .withStartAction {
//                        Log.d("Creditos", "Animación del logo comenzó")
//                    }
//                    .withEndAction {
//                        Log.d("Creditos", "Animación del logo terminó - Y final: ${imagenFinal.translationY}")
//                    }
//                    .start()
//            }
//        }, tiempoHastaMitad)
//    }
//
//
//
//    }

class CreditosActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var clickCount = 0
    private val clickResetDelay = 2000L
    private val handler = Handler(Looper.getMainLooper())
    private var resetRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_creditos)

        initMediaPlayer()
        val container = findViewById<FrameLayout>(R.id.creditosContainer)
        val imagenFinal = findViewById<ImageView>(R.id.logoFinal)

        val lineas = getString(R.string.creditos).split("\n")
        val screenHeight = resources.displayMetrics.heightPixels

        mostrarCreditos(container, lineas, screenHeight)
        animaLogo(imagenFinal, screenHeight, lineas)
        huevoPascua(imagenFinal)
    }

    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.soni).apply {
            isLooping = false
            setVolume(1.0f, 1.0f)
        }
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
     * @param posicionInicial la posición vertical inicial desde la que comenzará la animación (fuera de pantalla).
     * @return se devuelve un nuevo TextView con la línea creada
     */
//    private fun crearLineaCredito(texto: String, posicionInicial: Float): TextView {
//        return TextView(this).apply {
//            text = texto
//            setTextColor(Color.WHITE)
//            textSize = 20f
//            gravity = Gravity.CENTER
//            textAlignment = View.TEXT_ALIGNMENT_CENTER
//            setTypeface(null, Typeface.BOLD)
//            setPadding(0, 20, 0, 20)
//            setSingleLine(false) // Permite múltiples líneas si el texto excede el ancho máximo.
//            ellipsize = null // No se recorta el texto con puntos suspensivos si es largo.
//            maxLines = Integer.MAX_VALUE
//            setLineSpacing(10f, 1f)
//            translationY = posicionInicial
//            visibility = View.INVISIBLE
//            layoutParams = FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.MATCH_PARENT,
//                FrameLayout.LayoutParams.WRAP_CONTENT
//            ).apply {
//                gravity = Gravity.CENTER_HORIZONTAL
//                setMargins(0, 20, 0, 20)
//            }
//        }
//    }

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
            duration = 30000L // la animación dura 30 segundos.
            startDelay = posicion * 500L // cada línea espera un poco más que la anterior (500 ms por posicion) para crear un efecto escalonado.
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
        val tiempoHastaMitad = (lineas.size - 1) * 500L + ((distanciaHastaMitad / distanciaTotal) * 30000L).toLong() // Calcula el tiempo en milisegundos en el que la última línea estará en el centro de la pantalla.

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
            mediaPlayer!!.start()
            true
        }
    }

}

