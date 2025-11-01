package antonio.femxa.appfinal


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
import kotlin.random.Random


class CategoriaActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var spCategorias: Spinner? = null
    private var intent: Intent? = null
    private var musicaOnOff: Boolean = true
    private lateinit var botonSonido: ImageButton
    private var mapaFb : Map<String, List<String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_categoria)

        // --- Recuperar estado de sonido desde SharedPreferences ---
        musicaOnOff = SonidoGestion.obtenerEstadoSonido(this)

        this.spCategorias = findViewById<View>(R.id.spinner_categorias) as Spinner

        mapaFb = PalabrasRepository.getMapaPalabras()
        loadSpinnerCategorias()

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
    /**
     * Cada vez que el activity vuelva de una pausa el spinner se coloca en la posicion selecciona una categoria
     */
    override fun onResume() {
        super.onResume()

        if (musicaOnOff && !SonidoGestion.musicaSonando()) {
            SonidoGestion.iniciarMusica(this, R.raw.categoria)
        }

        val spinner = findViewById<View>(R.id.spinner_categorias) as Spinner
        spinner.setSelection(0)


    }

    override fun onPause() {
        super.onPause()

        SonidoGestion.pausarMusica()
    }

    /**
     * Cargamos el spinner con el array que esta en categorias.xml
     */
    fun loadSpinnerCategorias() {

        var adapter: ArrayAdapter<CharSequence>? = null
        mapaFb?.let { mapa ->
            //si tengo info de Firebase
            val listaCategorias = mapa.keys.toList().toMutableList() // mapa es no nulo
            //OJO hay que añadir una primera posición falsa, como menú del listado
            listaCategorias.add(0, "Selecciona categoría \uD83E\uDDE9")

            adapter = ArrayAdapter(
                this,                                  // Contexto
                R.layout.spinner_item_dropdown,//android.R.layout.simple_spinner_item,  // Layout base para los ítems
                listaCategorias.toList()//la hacemos inmutable otra vez                             // Tu lista de Strings
            )
        } ?: run {
            //si no tengo info de Firebase, tiro de local
            adapter = ArrayAdapter.createFromResource(this, R.array.categorias, android.R.layout.simple_spinner_item)

        }
        //adapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter!!.setDropDownViewResource(R.layout.spinner_item_dropdown)
        spCategorias!!.adapter = adapter
        spCategorias!!.setOnItemSelectedListener(this)
    }

    /**
     * Cada vez que se cambie el spinner y no sea la posicion 0(selecciona una categoria) carga
     * el array conrrespondiente de esa categoria, obtiene un string aleatorio de ella
     * y se redirige a activity_tablero con el string conseguido
     * @param parent
     * @param view
     * @param pos La posicion del array de categorias
     * @param id
     */
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {

        var arrayPalabrasSeleccionado: Array<out CharSequence>? = null

        if (pos != 0) {


            mapaFb?.let { mapa ->
                //si tengo info de Firebase
                //por ser la primera posición ficticia para el títlo del spinner
                //hay que coger del mapa la posición menos 1, para que coincida
                var categoriaElegida = mapa.keys.toList().get(pos-1)
                arrayPalabrasSeleccionado = mapa.get(categoriaElegida)!!.toTypedArray()

            } ?: run {
                //si no tengo info de Firebase, tiro de local
                val array_categorias = resources.obtainTypedArray(R.array.array_categorias)
                arrayPalabrasSeleccionado = array_categorias.getTextArray(pos)
                array_categorias.recycle()

            }

            val palabra = palabraOculta(arrayPalabrasSeleccionado)

            Log.d("MIAPP", palabra)


            intent = Intent(this@CategoriaActivity, TableroActivity::class.java)

            intent!!.putExtra("palabra_clave", palabra)

            val spinner = findViewById<View>(R.id.spinner_categorias) as Spinner

            val aa = spinner.selectedItem.toString()
            intent!!.putExtra("categoria_seleccionada", aa)

            SonidoGestion.detenerMusica()
            startActivity(intent)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
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