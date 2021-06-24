package br.edu.infnet.anotations.domain.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.infnet.anotations.R
import br.edu.infnet.anotations.domain.models.Anotation

class GetAllAnotationsAdapter(val anotations: ArrayList<Anotation>, ) : RecyclerView.Adapter<GetAllAnotationsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GetAllAnotationsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: GetAllAnotationsAdapter.ViewHolder, position: Int) {
        val archive = anotations[position]
        holder.titulo.text = archive.title
        holder.description.text = archive.description
        holder.data.text = archive.date
        holder.img.setImageBitmap(archive.img)
    }

    override fun getItemCount(): Int = anotations.size

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var titulo : TextView
        internal var description: TextView
        internal var data : TextView
        internal var img : ImageView

        init {
            titulo = view.findViewById(R.id.cardTitle)
            description = view.findViewById(R.id.cardDescription)
            data = view.findViewById(R.id.cardData)
            img = view.findViewById(R.id.cardImage)
        }
    }
}