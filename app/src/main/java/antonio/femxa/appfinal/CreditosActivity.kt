package antonio.femxa.appfinal

//import android.R
import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ListView
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.NonCancellable.start


class CreditosActivity : AppCompatActivity() {
    var lv: ListView? = null
    var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_creditos)
//        val v = findViewById<View>(R.id.boton1) //castin
//        val boton = v as Button

        mediaPlayer = MediaPlayer.create(this, R.raw.soni)
        mediaPlayer!!.isLooping = false
        mediaPlayer!!.setVolume(100f, 100f)

//        boton.setOnLongClickListener {
//            mediaPlayer!!.start()
//            true
//        }

        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        val creditosText = findViewById<TextView>(R.id.creditosText)

        creditosText.post {
            val distance = creditosText.height + scrollView.height

            val fadeIn = AlphaAnimation(0f, 1f).apply {
                duration = 2000 // 2 segundos
                fillAfter = true
            }
//            creditosText.startAnimation(fadeIn)
            scrollView.startAnimation(fadeIn)

            ObjectAnimator.ofInt(scrollView, "scrollY", 0, distance).apply {
                duration = 35000L // 20 segundos
                interpolator = LinearInterpolator()
                start()
            }

            // 3. Fade-out justo antes de que termine el scroll
            val fadeOut = AlphaAnimation(1f, 0f).apply {
                duration = 2000
                fillAfter = true
            }

            Handler(Looper.getMainLooper()).postDelayed({
//                creditosText.startAnimation(fadeOut)
                scrollView.startAnimation(fadeOut)
            }, 33000) // 35s - 2s = 33s
        }

//        val scrollView = findViewById<ScrollView>(R.id.scrollView)
//        val creditosText = findViewById<TextView>(R.id.creditosText)
//
//        val lineas = getString(R.string.creditos).split("\n")
//        creditosText.text = "" // Vacía el texto
//
//        val handler = Handler(Looper.getMainLooper())
//        var delay = 0L
//
//        for (linea in lineas) {
//            handler.postDelayed({
//                creditosText.append("$linea\n")
//            }, delay)
//            delay += 500 // medio segundo entre líneas
//        }

    }


}