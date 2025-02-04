package com.example.texnostrelka_2025_otbor.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.interfaces.OnItemClickListener
import com.example.texnostrelka_2025_otbor.models.komiks_main


class komiksAdapter(private val komikslist: MutableList<komiks_main>, private val listener: OnItemClickListener) : RecyclerView.Adapter<komiksAdapter.KomiksViewHolder>() {

    inner class KomiksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.komiks_name)
        val textViewDesc: TextView = itemView.findViewById(R.id.komiks_description)
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val id = komikslist[position].id!!
                    val name = komikslist[position].text!!
                    val desc = komikslist[position].description!!
                    listener.onItemClick(id, name, desc)
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