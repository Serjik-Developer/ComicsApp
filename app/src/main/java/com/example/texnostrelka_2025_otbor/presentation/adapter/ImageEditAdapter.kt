package com.example.texnostrelka_2025_otbor.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.data.model.ImageModel
import com.example.texnostrelka_2025_otbor.data.remote.model.page.PageFromNetwork
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemClickListener

class ImageEditAdapter(private val images: List<ImageModel>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<ImageEditAdapter.ImageEditViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageEditViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_edit_one_image, parent, false)
        return ImageEditViewHolder(view, listener)
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ImageEditViewHolder, position: Int) {
        holder.bind(images[position])
    }

    inner class ImageEditViewHolder(itemView: View, private val listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        private val imageView = itemView.findViewById<ImageView>(R.id.editImageView)
        private val deleteBtn = itemView.findViewById<ImageButton>(R.id.btnDelete) // Исправлено на btnDelete
        private val editBtn = itemView.findViewById<ImageButton>(R.id.btnEdit)

        fun bind(imageModel: ImageModel) {
            imageModel?.let {
                imageView.setImageBitmap(it.image)
                // Установите фиксированные размеры для изображения
                imageView.layoutParams.width = 300 // или нужный вам размер
                imageView.layoutParams.height = 300
                imageView.requestLayout()

                deleteBtn.setOnClickListener {
                    listener.onDeleteClick(imageModel.id.toString())
                }
                editBtn.setOnClickListener {
                    listener.onEditClick(imageModel.id.toString())
                }
            }
        }
    }
}