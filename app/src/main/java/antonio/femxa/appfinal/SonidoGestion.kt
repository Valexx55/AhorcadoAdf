package antonio.femxa.appfinal

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer

object SonidoGestion {

    private var mediaPlayer: MediaPlayer? = null
    private var recursoActual: Int? = null
    private var sonando = false

    private const val PREFS_NAME = "preferencias_sonido"
    private const val KEY_SONIDO_ACTIVO = "sonido_activo"

    // --- CONTROL DE MÚSICA ---

    fun iniciarMusica(context: Context, idRecurso: Int) {
        if (!obtenerEstadoSonido(context)) return

        if (recursoActual != idRecurso) {
            detenerMusica()
            mediaPlayer = MediaPlayer.create(context, idRecurso)
            recursoActual = idRecurso
            mediaPlayer?.isLooping = true
            mediaPlayer?.setVolume(1.0f, 1.0f)
            mediaPlayer?.start()
            sonando = true
        } else if (mediaPlayer != null && !mediaPlayer!!.isPlaying) {
            mediaPlayer!!.start()
            sonando = true
        }
    }

    fun iniciarMusica(context: Context, idRecurso: Int, bucle:Boolean) {
        if (!obtenerEstadoSonido(context)) return

        if (recursoActual != idRecurso) {
            detenerMusica()
            mediaPlayer = MediaPlayer.create(context, idRecurso)
            recursoActual = idRecurso
            mediaPlayer?.isLooping = bucle
            mediaPlayer?.setVolume(1.0f, 1.0f)
            mediaPlayer?.start()
            sonando = true
        } else if (mediaPlayer != null && !mediaPlayer!!.isPlaying) {
            mediaPlayer!!.start()
            sonando = true
        }
    }

    fun pausarMusica() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            sonando = false
        }
    }

    fun detenerMusica() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        recursoActual = null
        sonando = false
    }

    /** 🔍 Devuelve si la música está sonando actualmente */
    fun musicaSonando(): Boolean = sonando

    // --- PREFERENCIAS DEL USUARIO ---

    /** Guarda el estado del sonido (true = activo, false = apagado) */
    fun guardarEstadoSonido(context: Context, activo: Boolean) {
        val prefs: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SONIDO_ACTIVO, activo).apply()
    }

    /** Obtiene el estado del sonido guardado (por defecto, true) */
    fun obtenerEstadoSonido(context: Context): Boolean {
        val prefs: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_SONIDO_ACTIVO, true)
    }

    /** Alterna el estado del sonido y lo guarda. Devuelve el nuevo estado. */
    fun alternarSonido(context: Context): Boolean {
        val nuevoEstado = !obtenerEstadoSonido(context)
        guardarEstadoSonido(context, nuevoEstado)
        if (!nuevoEstado) {
            pausarMusica()
        } else {
            recursoActual?.let { iniciarMusica(context, it) }
        }
        return nuevoEstado
    }
}
