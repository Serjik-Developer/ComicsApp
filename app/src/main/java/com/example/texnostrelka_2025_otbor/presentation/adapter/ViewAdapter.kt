package com.example.texnostrelka_2025_otbor.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.data.model.Page
import com.example.texnostrelka_2025_otbor.domain.repository.ComicsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewAdapter(
    private val context: Context,
    private var items: MutableList<Page>,
    private val comicsRepository: ComicsRepository,
    private val coroutineScope: CoroutineScope
) : RecyclerView.Adapter<ViewAdapter.ViewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pages_item_view, parent, false)
        return ViewViewHolder(context, view, comicsRepository, coroutineScope)
    }

    override fun onBindViewHolder(holder: ViewViewHolder, position: Int) {
        holder.bind(items[position])
    }
    fun updateData(newItems: MutableList<Page>) {
        items = newItems
        notifyDataSetChanged() // Уведомляем адаптер об изменениях
    }

    override fun getItemCount(): Int = items.size

    class ViewViewHolder(
        private val context: Context,
        itemView: View,
        private val comicsRepository: ComicsRepository,
        private val coroutineScope: CoroutineScope
    ) : RecyclerView.ViewHolder(itemView) {
        private val imageRecyclerView: RecyclerView = itemView.findViewById(R.id.imageRecyclerViewView)
        fun bind(item: Page) {
            coroutineScope.launch {
                val imageList = comicsRepository.getAllImagesOnPage(item.pageId)

                withContext(Dispatchers.Main) {
                    imageRecyclerView.layoutManager = GridLayoutManager(context, item.columns)
                    imageRecyclerView.adapter = ImageAdapter(imageList)
                }
            }
        }
    }
}