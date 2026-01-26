package antonio.femxa.appfinal

//import android.R
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.os.Build
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


class DerrotaActivity : AppCompatActivity() {
    private var palabra: String? = null
    private var mediaPlayer: MediaPlayer? = null
    private var intent: Intent? = null
    private var musicaOnOff: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_derrota)
        val imageView = findViewById<View>(R.id.imagenDerrota) as ImageView


        imageView.setBackgroundResource(R.drawable.progress_animation_gameover)
        val progressAnimation = imageView.background as AnimationDrawable
        progressAnimation.start()

        palabra = getIntent().getStringExtra("palabra_clave")

        val linearInicio = findViewById<LinearLayout>(R.id.boton_derrota_inicio)
        val linearBotonMasInfo = findViewById<LinearLayout>(R.id.boton_mas_info)
        val linearAlAzar: LinearLayout = findViewById<LinearLayout>(R.id.botonAzar)
        val textView = findViewById<View>(R.id.text_palabra_oculta) as TextView

        textView.text = palabra!!.uppercase(Locale.getDefault())

        // aquí añado el código para "Más Info"
        linearBotonMasInfo.setOnClickListener {
            val palabraSeleccionada = textView.text.toString()

            if (palabraSeleccionada.isNotEmpty()) {
                val intent = Intent(this@DerrotaActivity, MasInformacionActivity::class.java)
                intent.putExtra(MasInformacionActivity.EXTRA_PALABRA, palabraSeleccionada)
                startActivity(intent)
            }
        }

        linearInicio.setOnClickListener {
            val intent = Intent(
                this@DerrotaActivity,
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
            // startActivity(intent)
            finish()
        }

        //acción botón hacia atrás
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(
                    this@DerrotaActivity,
                    CategoriaActivity::class.java
                )

                startActivity(intent)
                finish()
            }
        })
    }

    override fun onResume() {
        super.onResume()

        musicaOnOff = getIntent().getBooleanExtra("SonidoOn-Off", true)

        mediaPlayer = MediaPlayer.create(this, R.raw.sonido_perdedor)
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

    /* override fun onBackPressed() {
         val intent = Intent(
             this@DerrotaActivity,
             CategoriaActivity::class.java
         )

         startActivity(intent)
         finish()
     }*/
}