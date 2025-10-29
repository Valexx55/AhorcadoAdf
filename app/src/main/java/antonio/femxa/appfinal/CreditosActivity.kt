package antonio.femxa.appfinal

//import android.R
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
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


        val container = findViewById<FrameLayout>(R.id.creditosContainer)
        val lineas = getString(R.string.creditos).split("\n")

        val screenHeight = resources.displayMetrics.heightPixels
        val startY = screenHeight.toFloat()
        val endY = -200f // fuera de pantalla por arriba

        lineas.forEachIndexed { index, texto ->
            val textView = TextView(this).apply {
                text = texto
                setTextColor(Color.YELLOW)
                textSize = 24f
                gravity = Gravity.CENTER
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                setTypeface(null, Typeface.BOLD)
                setPadding(0, 20, 0, 20)
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                }
            }

            container.addView(textView)

            // Animación personalizada
            val animator = ValueAnimator.ofFloat(startY, endY).apply {
                duration = 30000L
                startDelay = index * 300L // escalonado
                interpolator = LinearInterpolator()

                addUpdateListener { animation ->
                    val y = animation.animatedValue as Float
                    textView.translationY = y

                    // Escala según posición vertical (más alto = más pequeño)
                    val progress = 1f - (y / screenHeight)
                    val scale = 1f - (progress * 0.7f) // reduce hasta 30% del tamaño
                    textView.scaleX = scale
                    textView.scaleY = scale
                }
            }

            animator.start()
        }

    }
}