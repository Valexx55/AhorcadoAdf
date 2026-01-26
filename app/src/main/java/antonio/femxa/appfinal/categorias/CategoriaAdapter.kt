package antonio.femxa.appfinal.categorias

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import antonio.femxa.appfinal.R

class CategoriaAdapter(
    val categorias: List<String>,
    val onClick: (String) -> Unit
) : RecyclerView.Adapter<CategoriaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria, parent, false)

        return CategoriaViewHolder(view)
    }

    override fun getItemCount() : Int {

        return categorias.size
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {

        val nombreCategoria = categorias[position]

        holder.rellenarCategoria(nombreCategoria, onClick)
    }
}