package antonio.femxa.appfinal

//import android.R
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import antonio.femxa.appfinal.categorias.CategoriaActivity
import java.util.Locale


class VictoriaActivity : AppCompatActivity() {
    private var palabra: String? = null
    private var musicaOnOff: Boolean = false
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_victoria)

        val imageView = findViewById<View>(R.id.imagenvictoria) as ImageView
        //esta sentencia condicional a continuación es incorrecta, puesto que la clase AnimationDrawable
        //está soportada desde la versión 1; por lo cual, la animación que consiste en una rotacin de foto
        //sería visible en cualquier dispositivo, carenciendo de sentido este if
        //  LO MISMO PARA DERROTA ACTIVITY: SOBRA
        imageView.setBackgroundResource(R.drawable.progress_animation_winner)
        val progressAnimation = imageView.background as AnimationDrawable
        progressAnimation.start()


        palabra = intent.getStringExtra("palabra_clave")

        val linearInicio = findViewById<LinearLayout>(R.id.boton_victoria_inicio)
        val linearMasInfo: LinearLayout = findViewById<LinearLayout>(R.id.boton_mas_info)
        val linearAlAzar: LinearLayout = findViewById<LinearLayout>(R.id.botonAzar)
        val textView = findViewById<View>(R.id.text_palabra_oculta_victoria) as TextView

        textView.text = palabra!!.uppercase(Locale.getDefault())

        // aquí añado el código para "Más Info"
        linearMasInfo.setOnClickListener {
            val palabraSeleccionada = textView.text.toString()

            if (palabraSeleccionada.isNotEmpty()) {
                val intent = Intent(this@VictoriaActivity, MasInformacionActivity::class.java)
                intent.putExtra(MasInformacionActivity.EXTRA_PALABRA, palabraSeleccionada)
                startActivity(intent)
            }
        }


        linearInicio.setOnClickListener {
            val intent = Intent(
                this@VictoriaActivity,
                CategoriaActivity::class.java
            )
            intent.putExtra("SonidoOn-Off", musicaOnOff)
            startActivity(intent)
            finish()
        }

        linearAlAzar.setOnClickListener {
            val intent = Intent(
                this,
                TableroActivity::class.java
            )
            intent.putExtra("alazar", true)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            finish()
        }


        //acción botón hacia atrás
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(
                    this@VictoriaActivity,
                    CategoriaActivity::class.java
                )

                startActivity(intent)

                finish()
            }
        })
    }

    override fun onResume() {
        super.onResume()

        musicaOnOff = intent.getBooleanExtra("SonidoOn-Off", true)

        mediaPlayer = MediaPlayer.create(this, R.raw.sonido_ganador)
        mediaPlayer!!.isLooping = false
        mediaPlayer!!.setVolume(100f, 100f)

        if (musicaOnOff) {
            mediaPlayer!!.start()
        }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer!!.stop()
    }

}