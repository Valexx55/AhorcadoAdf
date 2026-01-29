package antonio.femxa.appfinal


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import antonio.femxa.appfinal.databinding.CardCategoriaBinding


class CategoriasAdapter(
    private val categorias: List<String>,
    private val onCategoriaClick: (String, Int) -> Unit
): RecyclerView.Adapter<CategoriaViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoriaViewHolder {
        val categoria = CardCategoriaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoriaViewHolder(categoria)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoria = categorias[position]
        holder.rellenarCardCategoria(categoria)

        // Al pulsar una opción
        holder.itemView.setOnClickListener {
            onCategoriaClick(categoria, position)
        }
    }

    override fun getItemCount(): Int = categorias.size
}