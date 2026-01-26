package antonio.femxa.appfinal.categorias

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import antonio.femxa.appfinal.R

class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoria)

    fun rellenarCategoria(nombreCategoria: String, onClick: (String) -> Unit) {

        tvCategoria.text = nombreCategoria

        itemView.setOnClickListener {

            onClick(nombreCategoria)
        }
    }
}