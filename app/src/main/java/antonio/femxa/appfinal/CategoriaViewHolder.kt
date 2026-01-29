package antonio.femxa.appfinal

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import antonio.femxa.appfinal.databinding.CardCategoriaBinding

class CategoriaViewHolder(val categoriaBinding: CardCategoriaBinding) : RecyclerView.ViewHolder(categoriaBinding.root) {

    fun rellenarCardCategoria(categoria: String) {
        Log.d("MIAPP", "Categoria = $categoria longitud ${categoria.length}")
        val nombrecategoria = categoria.substringBeforeLast(" ")
        val dibujocategoria = categoria.substringAfterLast(" ")

        categoriaBinding.textCategoria.text = nombrecategoria
        categoriaBinding.icon.text = dibujocategoria
    }
}