package antonio.femxa.appfinal

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MasInformacionActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PALABRA = "palabra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mas_info)

        val webView: WebView = findViewById(R.id.webview_mas_info)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        val palabra = intent.getStringExtra(EXTRA_PALABRA)
        palabra?.let {
            try {
                val url = "https://www.google.com/search?tbm=isch&q=$it"
                webView.loadUrl(url)
            } catch (e: Exception)
            {
                Log.e("MIAPP","Fallo al cargar la página de más info ${e.message} ", e)
            }

        }

        // Botón "Volver al inicio"
        val botonVolver: Button = findViewById(R.id.boton_volver)
        botonVolver.setOnClickListener {
            finish() // cierra esta actividad y vuelve a la anterior
        }
    }
}