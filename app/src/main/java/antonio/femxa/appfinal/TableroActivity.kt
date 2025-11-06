package antonio.femxa.appfinal

//import android.R
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale
import android.view.ContextThemeWrapper
import android.view.WindowMetrics
import androidx.activity.enableEdgeToEdge
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import java.text.Normalizer
import kotlin.random.Random


class TableroActivity : AppCompatActivity() {

    private var palabraNormalizada: String? = null
    private var palabraOriginal: String? = null
    private val array_pics = intArrayOf(
        R.drawable.ic_cuerda,
        R.drawable.ic_cabeza,
        R.drawable.ic_cuerpo,
        R.drawable.ic_brazo,
        R.drawable.ic_brazos,
        R.drawable.ic_pierna
    )
    private var contador: Int = 0
    private var tamaño_palabra: Int = 0
    private var contador_aciertos: Int = 0
    private var intent: Intent? = null
    //private var sonidoOnOff: Boolean = false
    private var musicaOnOff: Boolean = true
    private val letras:List<Char> = listOf('A','B','C','D','E','F','G','H','I','J','K','L','M','N','Ñ','O','P','Q','R','S','T','U','V','W','X','Y','Z') // Letras que componen el teclado
    private lateinit var botonSonido: ImageButton
    var categoria:String = ""

    val idUnitAdBanner = "ca-app-pub-9910445535228761/9528197815"

    lateinit var adView: View

    private val adSize: AdSize
        get() {
            val displayMetrics = resources.displayMetrics
            val adWidthPixels =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val windowMetrics: WindowMetrics = this.windowManager.currentWindowMetrics
                    windowMetrics.bounds.width()
                } else {
                    displayMetrics.widthPixels
                }
            val density = displayMetrics.density
            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }


    fun crearAnuncio ()
    {

        //obtengo un anuncio
        val adView = AdView(this)
        adView.adUnitId = idUnitAdBanner
        adView.setAdSize(adSize)
        this.adView = adView

        //refresco el XML
        val adviewxml = findViewById<AdView> (R.id.anuncio)
        adviewxml.removeAllViews()
        adviewxml.addView(this.adView)

        //cargo el anuncio
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)



        adView.adListener = object: AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.d("MIAPP","onAdClicked()" )
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.d("MIAPP","onAdClosed()" )
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                // Code to be executed when an ad request fails.
                Log.d("MIAPP","onAdFailedToLoad()" )
            }

            override fun onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
                Log.d("MIAPP","onAdImpression()" )
            }

            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.d("MIAPP","onAdLoaded()" )
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.d("MIAPP","onAdOpened()" )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tablero)

        val v = findViewById<View>(R.id.btnImagen)
        val ib = v as ImageButton

        crearAnuncio()
        contador = 0
        contador_aciertos = 0

        var alazar = getIntent().getBooleanExtra("alazar", false)

        if (alazar)
        {
            val mapaPalabras = PalabrasRepository.getMapaPalabras()
            categoria = mapaPalabras?.keys?.random().toString()
            palabraOriginal = mapaPalabras?.get(categoria)?.random(Random(System.nanoTime()))

        } else {
            palabraOriginal = getIntent().getStringExtra("palabra_clave")
            categoria = getIntent().getStringExtra("categoria_seleccionada").toString()

        }


        Log.d("MIAPP", "Palabra Orignal al inicio = $palabraOriginal")

        palabraNormalizada = quitarTildesYMayusculas(palabraOriginal!!)

        Log.d("MIAPP", "Palabra Orignal al inicio2 = $palabraOriginal")
        Log.d("MIAPP", "Palabra Normalizada  = $palabraNormalizada")

        // --- Recuperar estado de sonido desde SharedPreferences ---
        musicaOnOff = SonidoGestion.obtenerEstadoSonido(this)

        botonSonido = findViewById<ImageButton>(R.id.btnImagen)

        // --- Configurar botón de sonido ---
        actualizarIconoBotonSonido()

        botonSonido.setOnClickListener {
            val sonidoActivo = SonidoGestion.alternarSonido(this)
            musicaOnOff = sonidoActivo
            actualizarIconoBotonSonido()

            if (sonidoActivo) {
                SonidoGestion.detenerMusica()
                SonidoGestion.iniciarMusica(this, R.raw.tablero)
            } else {
                SonidoGestion.pausarMusica()
            }
        }



        // --- Botón atrás ---
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@TableroActivity, CategoriaActivity::class.java)
                intent.putExtra("SonidoOn-Off", SonidoGestion.musicaSonando())
                startActivity(intent)
                finish()
            }
        })

        // --- Iniciar música si estaba activa ---
        if (musicaOnOff && !SonidoGestion.musicaSonando()) {
            SonidoGestion.iniciarMusica(this, R.raw.tablero)
        }


        //palabraOriginal = palabraFactorizada

        tamaño_palabra = obtenerTamañoPalabra(palabraOriginal!!)

        val imageView = findViewById<View>(R.id.imagenes_ahorcado) as ImageView
        imageView.setImageResource(array_pics[contador])


        dibujarPanel(palabraNormalizada)
        var fila1: TableRow? = findViewById<View>(R.id.lugar_inflado) as TableRow
        var fila2: TableRow? = findViewById<View>(R.id.lugar_inflado2) as TableRow
        var fila3: TableRow? = findViewById<View>(R.id.lugar_inflado3) as TableRow
        var fila4: TableRow? = findViewById<View>(R.id.lugar_inflado4) as TableRow

        fila1 = if ((fila1!!.childCount == 0)) null else fila1
        fila2 = if ((fila2!!.childCount == 0)) null else fila2
        fila3 = if ((fila3!!.childCount == 0)) null else fila3
        fila4 = if ((fila4!!.childCount == 0)) null else fila4

        identificarEditText(fila1, fila2, fila3, fila4)
//        pintarFondoEnModoOscuro (fila1, fila2, fila3, fila4)
        ocultarEspacios(palabraNormalizada)

        val textViewCategoria = findViewById<View>(R.id.textviewcategoria) as TextView


        textViewCategoria.text = categoria

        dibujarTeclado(letras)
    }

    private fun pintarFondoEnModoOscuro(
        fila1: TableRow?,
        fila2: TableRow?,
        fila3: TableRow?,
        fila4: TableRow?
    ) {

        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> {
                // 🌙 Modo oscuro activo
                fila1?.setBackgroundColor(Color.argb(128, 128, 128, 128))
                fila2?.setBackgroundColor(Color.argb(128, 128, 128, 128))
                fila3?.setBackgroundColor(Color.argb(128, 128, 128, 128))
                fila4?.setBackgroundColor(Color.argb(128, 128, 128, 128))
            }
        }



    }

    /**
     * "selecciona categoría" -> "\uD83E\uDDC9 $cat"     // 🧩
     * "animales" -> "\uD83E\uDD8A $cat"      // 🦊
     * "deportes" -> "\uD83C\uDFC5 $cat"      // 🏅
     * "eñe palabras" -> "\uD83E\uDDEA $cat"  // 🧠
     * "famosos" -> "\uD83C\uDF1F $cat"       // 🌟
     * "frutas" -> "\uD83C\uDF4E $cat"        // 🍎
     * "lugares" -> "\uD83D\uDDFA\uFE0F $cat" // 🗺️
     * "películas" -> "\uD83C\uDFAC $cat"     // 🎬
     * "internet" -> "\uD83C\uDF10 $cat"      // 🌐
     * "estilos musicales" -> "\uD83C\uDFB5 $cat" // 🎵
     * else -> "\u2B50 ${cat.replaceFirstChar { it.uppercase() }}" // ⭐
     */
    /**
     * Dibuja el teclado del juego.
     * @param letras las letras que conforman la distribución del teclado.
     */
    private fun dibujarTeclado(letras:List<Char>)
    {
        val gridLayout = findViewById<GridLayout>(R.id.teclado)

        letras.forEachIndexed { index, letra ->
            val tecla = crearTecla(letra, index, this)
            gridLayout.addView(tecla)
        }
    }

    fun mostrarLetra(letra: String, palabra: String) {
        val letrita = letra[0]
        for (i in 0 until palabra.length) {
            if (letrita == palabra[i]) {
                val et = findViewById<View>(i) as EditText
                et.apply {
                    setText(letrita.toString() + "")
                }
                Log.d("MENSAJE", "HA ENCONTRADO LA LETRA $letrita")
            }
        }
    }

    fun ocultarEspacios(palabra: String?) {
        val letrita = ' '
        for (i in 0 until palabra!!.length) {
            if (letrita == palabra[i]) {
                val et = findViewById<View>(i) as EditText
                et.visibility = View.INVISIBLE
                Log.d("MENSAJE", "HA ENCONTRADO LA LETRA $letrita")
            }
        }
    }

    private fun dibujarPanel(palabra_oculta: String?) {
        val fila1 = findViewById<View>(R.id.lugar_inflado) as ViewGroup
        val fila2 = findViewById<View>(R.id.lugar_inflado2) as ViewGroup
        val fila3 = findViewById<View>(R.id.lugar_inflado3) as ViewGroup
        val fila4 = findViewById<View>(R.id.lugar_inflado4) as ViewGroup

        val longi_palabra = palabra_oculta!!.length
        val layoutInflater = this@TableroActivity.layoutInflater //o LayoutInflater.from(a)

        val lista_palabra =
            palabra_oculta.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        /*for (int z = 0; z < lista_palabra.length-1; z++)
       {
           lista_palabra[z] = lista_palabra[z] + " ";
       }*/
        var pos_palabra = 0
        var n_linea = 1
        var caracteres_linea_actual = 0


        for (i in 0 until longi_palabra) {
            // CONTAR LÍNEAS CON SWITCH
            when (n_linea) {
                1 -> if (palabra_oculta[i] == ' ') {
                    pos_palabra++
                    val v1: View = layoutInflater.inflate(R.layout.panel, fila1, true)
                    if (lista_palabra[pos_palabra].length + caracteres_linea_actual < 10) {
                        caracteres_linea_actual++
                        Log.d(
                            "MENSAJE",
                            "if Case 1: letra " + palabra_oculta[i] + ", linea " + n_linea
                        )
                    } else {
                        caracteres_linea_actual = 1
                        n_linea = 2
                        Log.d(
                            "MENSAJE",
                            "Else Case 1: letra " + palabra_oculta[i] + ", linea " + n_linea
                        )
                    }
                } else {
                    val v1: View = layoutInflater.inflate(R.layout.panel, fila1, true)
                    caracteres_linea_actual++
                    Log.d("MENSAJE", "Case 1: letra " + palabra_oculta[i] + ", linea " + n_linea)
                }

                2 -> if (palabra_oculta[i] == ' ') {
                    pos_palabra++
                    val v1: View = layoutInflater.inflate(R.layout.panel, fila2, true)
                    if (lista_palabra[pos_palabra].length + caracteres_linea_actual < 10) {
                        caracteres_linea_actual++
                        Log.d(
                            "MENSAJE",
                            "Case 2: letra " + palabra_oculta[i] + ", linea " + n_linea
                        )
                    } else {
                        caracteres_linea_actual = 1
                        n_linea = 3
                        Log.d(
                            "MENSAJE",
                            "Else Case 2: letra " + palabra_oculta[i] + ", linea " + n_linea
                        )
                    }
                } else {
                    val v1: View = layoutInflater.inflate(R.layout.panel, fila2, true)
                    caracteres_linea_actual++
                    Log.d("MENSAJE", "Case 2: letra " + palabra_oculta[i] + ", linea " + n_linea)
                }


                3 -> if (palabra_oculta[i] == ' ') {
                    pos_palabra++

                    val v1: View = layoutInflater.inflate(R.layout.panel, fila3, true)
                    if (lista_palabra[pos_palabra].length + caracteres_linea_actual < 10) {
                        caracteres_linea_actual++
                        Log.d(
                            "MENSAJE",
                            "Case 3: letra " + palabra_oculta[i] + ", linea " + n_linea
                        )
                    } else {
                        caracteres_linea_actual = 1
                        n_linea = 4
                        Log.d(
                            "MENSAJE",
                            "Else Case 3: letra " + palabra_oculta[i] + ", linea " + n_linea
                        )
                    }
                } else {
                    val v1: View = layoutInflater.inflate(R.layout.panel, fila3, true)
                    caracteres_linea_actual++
                    Log.d("MENSAJE", "Case 3: letra " + palabra_oculta[i] + ", linea " + n_linea)
                }


                4 -> if (palabra_oculta[i] == ' ') {
                    pos_palabra++

                    val v1: View = layoutInflater.inflate(R.layout.panel, fila4, true)
                    if (lista_palabra[pos_palabra].length + caracteres_linea_actual < 10) {
                        caracteres_linea_actual++
                        Log.d(
                            "MENSAJE",
                            "Case 4: letra " + palabra_oculta[i] + ", linea " + n_linea
                        )
                    } else {
                        Log.d("MENSAJE", "La cadena tiene más extensión de la permitida")
                    }
                } else {
                    val v1: View = layoutInflater.inflate(R.layout.panel, fila4, true)
                    caracteres_linea_actual++
                    Log.d("MENSAJE", "Case 4: letra " + palabra_oculta[i] + ", linea " + n_linea)
                }

                else -> {}
            }
        }
    }

    fun identificarEditText(
        rowLugarInflado1: ViewGroup?,
        rowLugarInflado2: ViewGroup?,
        rowLugarInflado3: ViewGroup?,
        rowLugarInflado4: ViewGroup?
    ) {
        var cont_aux = 0

        try {
            for (i in 0 until rowLugarInflado1!!.childCount) {
                val linear = rowLugarInflado1.getChildAt(i) as ViewGroup
                val et = linear.getChildAt(0) as EditText
                et.id = i
                Log.d("MENSAJE", "editado EditText n: $i")
                Log.d("MENSAJE", "Id de EditText: " + et.id)
                cont_aux++
            }

            Log.d("MENSAJE", rowLugarInflado2.toString())
            if (rowLugarInflado2 != null) {
                for (i in 0 until (rowLugarInflado2.childCount)) {
                    val linear = rowLugarInflado2.getChildAt(i) as ViewGroup
                    val et = linear.getChildAt(0) as EditText
                    Log.d("MENSAJE", et.toString())
                    et.id = cont_aux
                    Log.d("MENSAJE", "editado EditText n: $cont_aux")
                    Log.d("MENSAJE", "Id de EditText: " + et.id)
                    cont_aux++
                }
            } else {
                Log.d("MENSAJE", "no hay segunda fila")
            }

            if (rowLugarInflado3 != null) {
                var cont_aux2 = 0
                cont_aux2 = cont_aux
                for (i in 0 until (rowLugarInflado3.childCount)) {
                    val linear = rowLugarInflado3.getChildAt(i) as ViewGroup
                    val et = linear.getChildAt(0) as EditText
                    et.id = cont_aux
                    Log.d("MENSAJE", "editado EditText n: $cont_aux")
                    Log.d("MENSAJE", "Id de EditText: " + et.id)
                    cont_aux++
                }
            } else {
                Log.d("MENSAJE", "no hay tercera fila")
            }

            if (rowLugarInflado4 != null) {
                var cont_aux2 = 0
                cont_aux2 = cont_aux
                for (i in 0 until (rowLugarInflado4.childCount)) {
                    val linear = rowLugarInflado4.getChildAt(i) as ViewGroup
                    val et = linear.getChildAt(0) as EditText
                    et.id = cont_aux
                    Log.d("MENSAJE", "editado EditText n: $cont_aux")
                    Log.d("MENSAJE", "Id de EditText: " + et.id)
                    cont_aux++
                }
            } else {
                Log.d("MENSAJE", "no hay cuarta fila")
            }
        } catch (t: Throwable) {
            Log.e("MENSAJE", "ERROR", t)
        }
    }

    fun escribirNumero(boton: View) {
        // declaramos variables y hacemos el casteo del boton para usarle
        //var palabra = getPalabra()
        Log.d("MENSAJE", palabraNormalizada!!)
        val btnPulsado = boton as Button
        val pulsado = btnPulsado.text.toString() //cogemos el texto del boton pulsado


        //nos creamos una variable boleana que nos dara si es falso o verdadero con lo que salga del metodo
        // haremos una condicion if en la que nos dira si la encuentra que cambie el texto del boton y lo ponga del color verde
        //sino que la ponga de color rojo y no deje pulsarla otra vez la deshabilita
        //palabra = palabra.uppercase(Locale.getDefault())
        val encontrada = buscarLetra(pulsado, palabraNormalizada!!)
        if (encontrada) {
            letraAcertada(btnPulsado)
            mostrarLetra(pulsado, palabraNormalizada!!)
        } else {
            letraFallada(btnPulsado)
        }
    }

    /**
     * Se cambia a verde el boton introducido y si el contador_aciertos es igual al tamaño de la palabra oculta
     * se redirige a VictoriaActivity
     * @param button
     */
    fun letraAcertada(button: Button) {
        button.setTextColor(Color.rgb(34, 153, 84))
        button.setBackgroundColor(Color.TRANSPARENT)

        Log.d("MENSAJE", "$contador_aciertos contador")
        Log.d("MENSAJE", "$tamaño_palabra tamaño")

        if (contador_aciertos == tamaño_palabra) {
            intent = Intent(this@TableroActivity, VictoriaActivity::class.java)

            intent!!.putExtra("palabra_clave", palabraOriginal)
            Log.d("MIAPP", "Palabra Orignal = $palabraOriginal")

            //intent!!.putExtra("SonidoOn-Off", sonidoOnOff)
            intent?.putExtra("SonidoOn-Off", SonidoGestion.musicaSonando())

            startActivity(intent)
        }
    }

    /**
     * Cambia el color a rojo e inutiliza el boton introducido, si el contador de fallos
     * es igual a 6 se redirige a DerrotaActivity, si no, cambia la imagen del ahoracado
     * @param button
     */
    fun letraFallada(button: Button) {
        button.setTextColor(Color.RED)
        button.setBackgroundColor(Color.TRANSPARENT)
        button.isEnabled = false
        contador++

        if (contador == 6) {
            intent = Intent(this@TableroActivity, DerrotaActivity::class.java)

            intent!!.putExtra("palabra_clave", palabraOriginal)

            //intent!!.putExtra("SonidoOn-Off", sonidoOnOff)
            intent?.putExtra("SonidoOn-Off", SonidoGestion.musicaSonando())
            Log.d("MIAPP", "Palabra Orignal = $palabraOriginal")

            startActivity(intent)
        } else {
            val imageView = findViewById<View>(R.id.imagenes_ahorcado) as ImageView
            imageView.setImageResource(array_pics[contador])
        }
    }

    /**
     * Busca en una palabra una letra introducidas, cada vez que encuentre esa letra en la
     * palabra se añade mas uno al contador_palabra_verdadero y devuelve un true
     * @param letra
     * @param palabra
     * @return
     */
    fun buscarLetra(letra: String, palabra: String): Boolean {
        var encontrado = false
        val letrita = letra[0]
        for (i in 0 until palabra.length) {
            Log.d("MIAPP", "Letrita $letrita Char en curso ${palabra[i]} son iguales ${letrita == palabra[i]}")
            if (letrita == palabra[i]) {
                encontrado = true
                contador_aciertos++
            }
        }

        return encontrado
    }


    /**
     * Dada una palabra introducida te dice su numero de letras, no cuenta los espacios
     * @param palabra
     * @return numero de posiciones de esa palabra
     */
    fun obtenerTamañoPalabra(palabra: String): Int {
        var palabra = palabra
        var contador = 0

        palabra = palabra.replace(" ", "")

        for (i in 0 until palabra.length) {
            contador++
        }

        return contador
    }

    /**
     * Cada vez que el activity vuelva de una pausa el spinner se coloca en la posicion selecciona una categoria
     */
    override fun onResume() {
        super.onResume()

        if (musicaOnOff && !SonidoGestion.musicaSonando()) {
            SonidoGestion.iniciarMusica(this, R.raw.tablero)
        }
    }

    override fun onPause() {
        super.onPause()
        //mediaPlayer!!.stop()
        SonidoGestion.pausarMusica()
    }

    private fun actualizarIconoBotonSonido() {
        if (musicaOnOff) {
            botonSonido.setImageResource(R.drawable.ic_volume_up)
        } else {
            botonSonido.setImageResource(R.drawable.ic_volume_off)
        }
    }

    /**
     * Dado un carácter, crea la tecla correspondiente del teclado a mostrar en el layout
     * @param letra la letra de la tecla
     * @param tag etiqueta asociada a la tecla
     * @param context contexto del teclado
     * @return La tecla creada.
     */
    fun crearTecla(letra: Char, tag: Int, context: Context): Button
    {
        val themedContext = ContextThemeWrapper(context, R.style.EstiloTecla)
        val button = Button(themedContext, null, R.style.EstiloTecla)

        button.text = letra.toString()
        button.tag = tag
        button.setTextColor(Color.WHITE)
        button.setOnClickListener{escribirNumero(it)}

        // Margen y distribución en GridLayout
        val params = GridLayout.LayoutParams().apply {
            width = 0
            height = GridLayout.LayoutParams.WRAP_CONTENT
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(4, 4, 4, 4)
        }

        button.layoutParams = params
        Log.d("MENSAJE", "crearTecla: Tecla ${button.text} creada")

        return button
    }



    fun quitarTildesYMayusculas(texto: String): String {
        var palabraNormalizada = String(texto.toCharArray())//copiamos un nuevo string para conservar el orginal

        val protegido = palabraNormalizada.replace("ñ", "{enie_min}").replace("Ñ", "{enie_may}")

        // Descomponemos para poder eliminar diacríticos del resto (áéíóú ü, etc.)
        val nfd = Normalizer.normalize(protegido, Normalizer.Form.NFD)

        // Restauramos ñ/Ñ antes de borrar diacríticos
        val restaurado = nfd
            .replace(Regex("n\\u0303"), "{enie_min}")
            .replace(Regex("N\\u0303"), "{enie_may}")

        // Eliminamos las demás marcas diacríticas
        val sinDiacriticos = restaurado.replace(Regex("[\\u0300-\\u036F]"), "")

        // Quitamos los placeholders y ponemos en MAYÚSCULAS (es-ES)
        return sinDiacriticos
            .replace("{enie_min}", "ñ")
            .replace("{enie_may}", "Ñ")
            .uppercase(Locale("es", "ES"))
    }


}