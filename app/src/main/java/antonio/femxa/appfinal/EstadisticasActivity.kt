package antonio.femxa.appfinal

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EstadisticasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MIAPP", "EstadisticasActivity onCreate")
        setContentView(R.layout.activity_estadisticas)

        val prefs = getSharedPreferences("stats", MODE_PRIVATE)
        val victorias = prefs.getInt("victorias", 0)
        val derrotas = prefs.getInt("derrotas", 0)
        val partidas = prefs.getInt("partidas", 0)
        val porcentaje = if (partidas > 0) (victorias.toFloat() / partidas * 100) else 0f

        val textVictorias = findViewById<TextView>(R.id.textVictorias)
        val textDerrotas = findViewById<TextView>(R.id.textDerrotas)
        val textPartidas = findViewById<TextView>(R.id.textPartidas)
        val textPorcentaje = findViewById<TextView>(R.id.textPorcentaje)
        val btnReiniciar = findViewById<Button>(R.id.btnReiniciar)

        textVictorias.text = "Victorias: $victorias"
        textDerrotas.text = "Derrotas: $derrotas"
        textPartidas.text = "Partidas jugadas: $partidas"
        textPorcentaje.text = "Porcentaje de victorias: ${"%.1f".format(porcentaje)}%"

        btnReiniciar.setOnClickListener {
            prefs.edit().clear().apply()
            recreate() // refresca la pantalla con valores reiniciados
        }
    }
}
