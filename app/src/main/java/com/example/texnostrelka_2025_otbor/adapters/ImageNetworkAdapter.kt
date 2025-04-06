package com.example.texnostrelka_2025_otbor.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.adapters.ImageNetworkAdapter.ImageNetworkViewHolder
import com.example.texnostrelka_2025_otbor.models.ImageModel
import com.example.texnostrelka_2025_otbor.models.NetworkModels.ImageNetworkModel
import kotlin.io.encoding.Base64

class ImageNetworkAdapter(private val imageList: MutableList<ImageNetworkModel>) : RecyclerView.Adapter<ImageNetworkViewHolder>(){
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

        fun String.base64ToBitmap(): Bitmap? {
            return try {
                val imageBytes = android.util.Base64.decode(this, android.util.Base64.DEFAULT)
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            } catch (e: Exception) {
                null
            }
        }

        fun bind(imageModel: ImageNetworkModel) {
            imageModel.image?.let { base64 ->
                imageView.setImageBitmap(base64.base64ToBitmap())
            }
        }
    }
}