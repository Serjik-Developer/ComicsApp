package com.example.texnostrelka_2025_otbor.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.data.mapper.convertNetworkToModel
import com.example.texnostrelka_2025_otbor.data.remote.model.page.PageFromNetworkModel
import com.example.texnostrelka_2025_otbor.presentation.adapter.EditPageAdapter.EditPageViewHolder
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemEditPageNetworkClickListener

class EditPageAdapter(
    private var pages: MutableList<PageFromNetworkModel>,
    private val listener: OnItemEditPageNetworkClickListener
) : RecyclerView.Adapter<EditPageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditPageViewHolder {
        Log.d("ADAPTER", "Creating ViewHolder")
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_edit_image, parent, false)
        return EditPageViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: EditPageViewHolder, position: Int) {
        Log.d("ADAPTER", "Binding ViewHolder at position $position")
        holder.bind(pages[position])
    }

    override fun getItemCount(): Int {
        Log.d("ADAPTER", "Item count: ${pages.size}")
        return pages.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newPages: MutableList<PageFromNetworkModel>) {
        Log.w("ADAPTER", "Updating data with ${newPages.size} pages")
        pages = newPages
        notifyDataSetChanged()
    }

    inner class EditPageViewHolder(
        itemView: View,
        private val listener: OnItemEditPageNetworkClickListener
    ) : RecyclerView.ViewHolder(itemView) {
        private val recyclerView = itemView.findViewById<RecyclerView>(R.id.editImageRecyclerView)

        fun bind(page: PageFromNetworkModel) {
            Log.w("DATA-ADAPTER", "Binding page: ${page.pageId}")
            val imageList = convertNetworkToModel(page.images, page.pageId)
            Log.w("DATA-ADAPTER", "Images count: ${imageList.size}, Columns: ${page.columns}")

            recyclerView.apply {
                val columns = if (page.columns > 0) page.columns else 1
                val rows = if (page.rows > 0) page.rows else 1
                layoutManager = GridLayoutManager(context, columns)

                adapter = ImageEditAdapter(page.pageId, imageList.toMutableList(), listener, rows, columns).apply {
                    registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                        override fun onChanged() {
                            super.onChanged()
                            Log.d("INNER_ADAPTER", "Data changed, item count: ${imageList.size}")
                        }
                    })
                }
            }
        }
    }
}