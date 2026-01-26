package antonio.femxa.appfinal

import androidx.recyclerview.widget.RecyclerView
import antonio.femxa.appfinal.databinding.CardCategoriaBinding

class CategoriaViewHolder(val categoriaBinding: CardCategoriaBinding) : RecyclerView.ViewHolder(categoriaBinding.root) {

    fun rellenarCardCategoria(categoria: String) {
        categoriaBinding.textCategoria.text = categoria
    }
}