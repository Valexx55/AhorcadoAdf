package antonio.femxa.appfinal

import android.os.Bundle
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
            val url = "https://www.google.com/search?tbm=isch&q=$it"
            webView.loadUrl(url)
        }

        // Botón "Volver al inicio"
        val botonVolver: Button = findViewById(R.id.boton_volver)
        botonVolver.setOnClickListener {
            finish() // cierra esta actividad y vuelve a la anterior
        }
    }
}