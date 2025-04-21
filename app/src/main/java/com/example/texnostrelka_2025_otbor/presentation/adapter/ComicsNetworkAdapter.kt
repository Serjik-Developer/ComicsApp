package com.example.texnostrelka_2025_otbor.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemClickListener
import com.example.texnostrelka_2025_otbor.data.remote.model.comic.ComicsCoverNetworkModel
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemComicsListener
import com.example.texnostrelka_2025_otbor.presentation.utils.base64ToBitmap

class ComicsNetworkAdapter(private var comics: MutableList<ComicsCoverNetworkModel>, private val listener: OnItemComicsListener, private val isMyComics: Boolean = false) : RecyclerView.Adapter<ComicsNetworkAdapter.ComiksViewHolder>() {

    inner class ComiksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.comics_network_name)
        val textViewDesc: TextView = itemView.findViewById(R.id.comics_network_description)
        val imageView: ImageView = itemView.findViewById(R.id.imageViewComics)
        private val deleteBtn: ImageButton = itemView.findViewById(R.id.delete_network_comics_btn)
        private val editBtn: ImageButton = itemView.findViewById(R.id.edit_netwotk_comics_btn)
        private val downloadBtn: ImageButton = itemView.findViewById(R.id.download_comics_btn)
        init {
            downloadBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDownloadClick(comics[position].id!!)
                }
            }
            if (isMyComics) {
                deleteBtn.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(comics[position].id!!)
                    }
                }
                editBtn.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onEditClick(comics[position].id!!)
                    }
                }
            }
            else {
                deleteBtn.visibility = View.GONE
                editBtn.visibility = View.GONE
            }
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val id = comics[position].id!!
                    listener.onItemClick(id)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newComics: MutableList<ComicsCoverNetworkModel>) {
        comics = newComics
        notifyDataSetChanged() // Уведомляем адаптер об изменениях
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComiksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comics_from_network, parent, false)
        return ComiksViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComiksViewHolder, position: Int) {
        val komiksItem = comics[position]
        holder.textView.text = komiksItem.text
        holder.textViewDesc.text = komiksItem.description
        komiksItem.image?.let { base64 ->
            holder.imageView.setImageBitmap(base64.base64ToBitmap())
        }
    }

    override fun getItemCount(): Int = comics.size
}