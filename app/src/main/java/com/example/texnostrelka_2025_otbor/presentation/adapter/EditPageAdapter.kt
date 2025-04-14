package com.example.texnostrelka_2025_otbor.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.data.model.ImageModel
import com.example.texnostrelka_2025_otbor.presentation.adapter.EditPageAdapter.EditPageViewHolder
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemClickListener

class EditPageAdapter(private var images: List<ImageModel>, private val listener: OnItemClickListener) : RecyclerView.Adapter<EditPageViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EditPageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_edit_image, parent, false)
        return EditPageViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(
        holder: EditPageViewHolder,
        position: Int
    ) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData (newImages: List<ImageModel>) {
        images = newImages
        notifyDataSetChanged()
    }

    inner class EditPageViewHolder(itemView: View, private val listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView){
        private val imageView = itemView.findViewById<ImageView>(R.id.imageViewEdit)
        private val editBtn = itemView.findViewById<ImageButton>(R.id.imageButtonEdit)
        private val deleteBtn = itemView.findViewById<ImageButton>(R.id.imageButtonDelete)

        fun bind(imageModel: ImageModel) {
            imageView.setImageBitmap(imageModel.image)
            editBtn.setOnClickListener {
                listener.onDeleteClick(imageModel.id.toString())
            }
            deleteBtn.setOnClickListener {
                listener.onEditClick(imageModel.id.toString())
            }
        }
    }
}