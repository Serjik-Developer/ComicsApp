package com.example.texnostrelka_2025_otbor.adapters

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.interfaces.OnItemClickListener
import com.example.texnostrelka_2025_otbor.models.NetworkModels.ComicsFromNetwork
import com.example.texnostrelka_2025_otbor.models.ComicsModel
import com.example.texnostrelka_2025_otbor.models.NetworkModels.ComicsNetworkModel
import com.example.texnostrelka_2025_otbor.utils.base64ToBitmap
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class ComicsNetworkAdapter(private var comics: MutableList<ComicsNetworkModel>, private val listener: OnItemClickListener, private val isMyComics: Boolean = false) : RecyclerView.Adapter<ComicsNetworkAdapter.ComiksViewHolder>() {

    inner class ComiksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.comics_network_name)
        val textViewDesc: TextView = itemView.findViewById(R.id.comics_network_description)
        val imageView: ImageView = itemView.findViewById(R.id.imageViewComics)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.delete_network_comics_btn)
        val editBtn: ImageButton = itemView.findViewById(R.id.edit_netwotk_comics_btn)
        init {
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
    fun updateData(newComics: MutableList<ComicsNetworkModel>) {
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