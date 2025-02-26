package com.example.texnostrelka_2025_otbor.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.database.ComicsDatabase
import com.example.texnostrelka_2025_otbor.interfaces.OnItemPageClickListener
import com.example.texnostrelka_2025_otbor.models.ImageModel
import com.example.texnostrelka_2025_otbor.models.Page
import com.example.texnostrelka_2025_otbor.models.PageWithImages

class PagesAdapter(
    private val context: Context,
    private var items: MutableList<Page>,
    private val listener: OnItemPageClickListener
) : RecyclerView.Adapter<PagesAdapter.PageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pages_item, parent, false)
        return PageViewHolder(context, view, listener)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(items[position])
    }
    fun updateData(newItems: MutableList<Page>) {
        items = newItems
        notifyDataSetChanged() // Уведомляем адаптер об изменениях
    }

    override fun getItemCount(): Int = items.size

    class PageViewHolder(
        private val context: Context,
        itemView: View,
        private val listener: OnItemPageClickListener
    ) : RecyclerView.ViewHolder(itemView) {
        private val imageRecyclerView: RecyclerView = itemView.findViewById(R.id.imageRecyclerView)
        private val editBtn: ImageButton = itemView.findViewById(R.id.edit_page_btn)
        private val deleteBtn: ImageButton = itemView.findViewById(R.id.delete_page_btn)

        fun bind(item: Page) {
            // Получаем все изображения для текущей страницы
            val database = ComicsDatabase(context)
            val imageList = database.getAllImagesOnPage(item.pageId)

            // Настраиваем RecyclerView для изображений
            imageRecyclerView.layoutManager = GridLayoutManager(context, item.columns)
            imageRecyclerView.adapter = ImageAdapter(context, imageList)

            // Обработка нажатий на кнопки
            deleteBtn.setOnClickListener {
                listener.onDeleteClick(item.pageId)
            }
            editBtn.setOnClickListener {
                listener.onEditClick(PageWithImages(item, imageList))
            }
        }
    }
}