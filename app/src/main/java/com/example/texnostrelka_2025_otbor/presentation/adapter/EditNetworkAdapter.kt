package com.example.texnostrelka_2025_otbor.presentation.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.data.remote.model.PageFromNetwork
import com.example.texnostrelka_2025_otbor.presentation.adapter.EditNetworkAdapter.EditNetworkViewHolder

class EditNetworkAdapter(private var pages: MutableList<PageFromNetwork>, private val context: Context): RecyclerView.Adapter<EditNetworkViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EditNetworkViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: EditNetworkViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int = pages.size
    inner class EditNetworkViewHolder(val itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {

    }
}