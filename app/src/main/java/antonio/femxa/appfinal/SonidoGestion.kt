package antonio.femxa.appfinal

import android.content.Context
import android.media.MediaPlayer

/**
 * Clase Object singleton que gestiona la música de fondo en toda la aplicación.
 * Se encarga de reproducir, pausar, detener y cambiar la música según la actividad.
 */
object SonidoGestion {

    private var mediaPlayer: MediaPlayer? = null
    private var estaSonando = false
    private var sonidoActualResId: Int? = null

    fun iniciarMusica(context: Context, resId: Int) {
        // Si ya está sonando la misma música, no reiniciamos
        if (estaSonando && sonidoActualResId == resId) return

        detenerMusica() // detiene cualquier sonido anterior si lo hubiese

        mediaPlayer = MediaPlayer.create(context.applicationContext, resId)
        mediaPlayer?.isLooping = true
        mediaPlayer?.setVolume(1.0f, 1.0f)
        mediaPlayer?.start()

        estaSonando = true
        sonidoActualResId = resId
    }

    fun pausarMusica() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) it.pause()
            }
        } catch (_: Exception) {}
        estaSonando = false
    }

    // por si quisiera reanudar en pausa no uso de momento
//    fun reanudarMusica() {
//        mediaPlayer?.start()
//        estaSonando = true
//    }

    fun detenerMusica() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
        estaSonando = false
        sonidoActualResId = null
    }

    /**
     * Cambia entre reproducir y pausar música. Devuelvo el estado final (true = sonando).
     */
    fun alternarMusica(context: Context, resId: Int): Boolean {
        if (estaSonando) {
            pausarMusica()
        } else {
            iniciarMusica(context, resId)
        }
        return estaSonando
    }

    fun musicaSonando(): Boolean {
        return estaSonando
    }
}