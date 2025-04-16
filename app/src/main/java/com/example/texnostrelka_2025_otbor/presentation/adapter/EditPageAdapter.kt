package com.example.texnostrelka_2025_otbor.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.data.mapper.convertNetworkToModel
import com.example.texnostrelka_2025_otbor.data.model.ImageModel
import com.example.texnostrelka_2025_otbor.data.remote.model.page.PageFromNetwork
import com.example.texnostrelka_2025_otbor.presentation.adapter.EditPageAdapter.EditPageViewHolder
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemClickListener

class EditPageAdapter(
    private var page: PageFromNetwork,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<EditPageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditPageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_edit_image, parent, false)
        return EditPageViewHolder(itemView, listener, parent.context)
    }

    override fun onBindViewHolder(holder: EditPageViewHolder, position: Int) {
        holder.bind(page)
    }

    override fun getItemCount(): Int = 10 // Оставляем 1, так как у вас одна страница с сеткой изображений

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newPage: PageFromNetwork) {
        Log.w("DATA", "DATA-UPDATED")
        page = newPage
        notifyDataSetChanged()
    }

    inner class EditPageViewHolder(itemView: View, private val listener: OnItemClickListener, private val context: Context) : RecyclerView.ViewHolder(itemView) {
        private val recyclerView = itemView.findViewById<RecyclerView>(R.id.editImageRecyclerView)

        fun bind(page: PageFromNetwork) {
            val pageId = page.pageId
            val imageList = convertNetworkToModel(page.images, pageId)
            Log.w("DATA-ADAPTER", "Images count: ${imageList.size}, Columns: ${page.columns}")

            recyclerView.apply {
                layoutManager = GridLayoutManager(context, page.columns).apply {
                    orientation = GridLayoutManager.VERTICAL
                }
                adapter = ImageEditAdapter(imageList, listener)
                setHasFixedSize(true)
            }
        }
    }
}