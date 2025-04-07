package com.example.texnostrelka_2025_otbor.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.adapters.PageNetworkAdapter.PageNetworkViewHolder
import com.example.texnostrelka_2025_otbor.models.NetworkModels.ComicsNetworkModel
import com.example.texnostrelka_2025_otbor.models.NetworkModels.PageFromNetwork

class PageNetworkAdapter(
    private var pages: MutableList<PageFromNetwork>,
    private val context: Context
) : RecyclerView.Adapter<PageNetworkViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageNetworkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pages_item_view, parent, false)
        return PageNetworkViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: PageNetworkViewHolder, position: Int) {
        holder.bind(pages[position])
    }

    override fun getItemCount(): Int = pages.size

    fun updateData(newPages: MutableList<PageFromNetwork>) {
        pages = newPages.toMutableList()
        notifyDataSetChanged()
    }

    class PageNetworkViewHolder(
        itemView: View,
        private val context: Context
    ) : RecyclerView.ViewHolder(itemView) {
        private val imageRecyclerView: RecyclerView = itemView.findViewById(R.id.imageRecyclerViewView)
        private var adapter: ImageNetworkAdapter? = null

        init {
            // Инициализация RecyclerView один раз в init
            imageRecyclerView.setHasFixedSize(true)
            adapter = ImageNetworkAdapter(mutableListOf())
            imageRecyclerView.adapter = adapter
        }

        fun bind(item: PageFromNetwork) {
            // Обновляем только данные адаптера
            val columns = if (item.columns == 0) 1 else item.columns
            imageRecyclerView.layoutManager = GridLayoutManager(context, columns)
            adapter?.updateData(item.images ?: mutableListOf())
        }
    }
}