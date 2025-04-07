package com.example.texnostrelka_2025_otbor.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.data.remote.model.ImageNetworkModel
import com.example.texnostrelka_2025_otbor.presentation.utils.base64ToBitmap

class ImageNetworkAdapter(
    private var imageList: MutableList<ImageNetworkModel>
) : RecyclerView.Adapter<ImageNetworkAdapter.ImageNetworkViewHolder>() {

    fun updateData(newImages: MutableList<ImageNetworkModel>) {
        imageList = newImages.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageNetworkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageNetworkViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageNetworkViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount(): Int = imageList.size
    class ImageNetworkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        fun bind(imageModel: ImageNetworkModel) {
            imageModel.image?.let { base64 ->
                imageView.setImageBitmap(base64.base64ToBitmap())
            }
        }
    }
}