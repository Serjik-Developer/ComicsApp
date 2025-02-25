package com.example.texnostrelka_2025_otbor.adapters

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.interfaces.OnItemClickListener
import com.example.texnostrelka_2025_otbor.models.ComicsModel

class komiksAdapter(private val komikslist: MutableList<ComicsModel>, private val listener: OnItemClickListener) : RecyclerView.Adapter<komiksAdapter.KomiksViewHolder>() {

    inner class KomiksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.komiks_name)
        val textViewDesc: TextView = itemView.findViewById(R.id.komiks_description)
        val delete_btn: ImageButton = itemView.findViewById(R.id.delete_comics_btn)
        val edit_btn: ImageButton = itemView.findViewById(R.id.edit_comics_btn)
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val id = komikslist[position].id!!
                    listener.onItemClick(id)
                }
            }
            delete_btn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val id = komikslist[position].id!!
                    listener.onDeleteClick(id)
                }
            }
            edit_btn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val id = komikslist[position].id!!
                    listener.onEditClick(id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KomiksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.komiks_rvview, parent, false)
        return KomiksViewHolder(view)
    }

    override fun onBindViewHolder(holder: KomiksViewHolder, position: Int) {
        val komiksItem = komikslist[position]
        holder.textView.text = komiksItem.text
        holder.textViewDesc.text = komiksItem.description
    }

    override fun getItemCount(): Int = komikslist.size
}