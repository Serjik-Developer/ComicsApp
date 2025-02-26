package com.example.texnostrelka_2025_otbor.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.database.ComicsDatabase
import com.example.texnostrelka_2025_otbor.models.Page

class ViewAdapter(
    private val context: Context,
    private var items: MutableList<Page>
) : RecyclerView.Adapter<ViewAdapter.ViewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pages_item_view, parent, false)
        return ViewViewHolder(context, view)
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
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        private val imageRecyclerView: RecyclerView = itemView.findViewById(R.id.imageRecyclerViewView)
        fun bind(item: Page) {
            // Получаем все изображения для текущей страницы
            val database = ComicsDatabase(context)
            val imageList = database.getAllImagesOnPage(item.pageId)

            // Настраиваем RecyclerView для изображений
            imageRecyclerView.layoutManager = GridLayoutManager(context, item.columns)
            imageRecyclerView.adapter = ImageAdapter(context, imageList)

        }
    }
}