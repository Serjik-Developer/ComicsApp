package com.example.texnostrelka_2025_otbor.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.data.model.ImageModel
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemEditPageNetworkClickListener

class ImageEditAdapter(
    private val pageId : String,
    private val images: MutableList<ImageModel>,
    private val listener: OnItemEditPageNetworkClickListener,
    private val rows: Int,
    private val columns: Int
) : RecyclerView.Adapter<ImageEditAdapter.ImageEditViewHolder>() {

    private val totalCells = rows * columns
    private val placeholderImages = mutableListOf<ImageModel?>()

    init {
        for (i in 0 until totalCells) {
            placeholderImages.add(null)
        }
        images.forEach { image ->
            if (image.cellIndex in 0 until totalCells) {
                placeholderImages[image.cellIndex] = image
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageEditViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_edit_one_image, parent, false)
        return ImageEditViewHolder(view, listener)
    }

    override fun getItemCount(): Int = totalCells

    override fun onBindViewHolder(holder: ImageEditViewHolder, position: Int) {
        val imageModel = placeholderImages[position]
        holder.bind(imageModel, position)
    }

    inner class ImageEditViewHolder(itemView: View, private val listener: OnItemEditPageNetworkClickListener) :
        RecyclerView.ViewHolder(itemView) {
        private val imageView = itemView.findViewById<ImageView>(R.id.editImageView)
        private val deleteBtn = itemView.findViewById<ImageButton>(R.id.btnDelete)
        private val editBtn = itemView.findViewById<ImageButton>(R.id.btnEdit)


        fun bind(imageModel: ImageModel?, cellIndex: Int) {
            if (imageModel != null) {
                imageView.setImageBitmap(imageModel.image)
                deleteBtn.visibility = View.VISIBLE
                editBtn.visibility = View.VISIBLE

                deleteBtn.setOnClickListener {
                    listener.onDeleteClick(imageModel.id.toString())
                }
                editBtn.setOnClickListener {
                    listener.onEditClick(imageModel.id.toString())
                }
            } else {
                imageView.setImageResource(R.drawable.gray_placeholder)
                deleteBtn.visibility = View.INVISIBLE
                editBtn.visibility = View.VISIBLE
                editBtn.setOnClickListener {
                    listener.onAddClick(pageId, cellIndex)
                }
            }
        }
    }
}