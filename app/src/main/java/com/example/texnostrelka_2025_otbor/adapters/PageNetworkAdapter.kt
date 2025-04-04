package com.example.texnostrelka_2025_otbor.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.adapters.PageNetworkAdapter.PageNetworkViewHolder
import com.example.texnostrelka_2025_otbor.models.NetworkModels.PageFromNetwork

class PageNetworkAdapter(private val pages: MutableList<PageFromNetwork>) : RecyclerView.Adapter<PageNetworkViewHolder>() {
    override fun getItemCount(): Int = pages.size

    override fun onBindViewHolder(holder: PageNetworkViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageNetworkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pages_item_view, parent, false)
        return PageNetworkViewHolder(view)
    }
    class PageNetworkViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageRecyclerView: RecyclerView =
    }
}