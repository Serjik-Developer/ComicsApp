package com.example.texnostrelka_2025_otbor.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.database.ComicsDatabase
import com.example.texnostrelka_2025_otbor.interfaces.OnItemPageClickListener
import com.example.texnostrelka_2025_otbor.models.ImageModel
import com.example.texnostrelka_2025_otbor.models.Page
import com.example.texnostrelka_2025_otbor.models.PageWithImages

class PagesAdapter(private val context: Context, private val items: MutableList<Page>,  private val listener: OnItemPageClickListener) : RecyclerView.Adapter<PagesAdapter.GridViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pages_item, parent, false)
        return GridViewHolder(context, view, listener)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class GridViewHolder(private val context: Context, itemView: View, private val listener: OnItemPageClickListener) : RecyclerView.ViewHolder(itemView) {
        private val gridLayout: GridLayout = itemView.findViewById(R.id.gridLayout)
        private val edit_btn: ImageButton = itemView.findViewById(R.id.edit_page_btn)
        private val delete_btn: ImageButton = itemView.findViewById(R.id.delete_page_btn)
        private lateinit var imageList: MutableList<ImageModel>
        fun bind(item: Page) {
            // Очищаем предыдущие элементы
            gridLayout.removeAllViews()

            // Устанавливаем количество строк и столбцов
            gridLayout.rowCount = item.rows
            gridLayout.columnCount = item.columns
            // Получаем все изображения из базы данных
            val database = ComicsDatabase(context)
            imageList = database.getAllImagesOnPage(item.pageId)
            // Динамически добавляем изображения в сетку
            for (i in 0 until imageList.size) {
                val imageView = ImageView(itemView.context).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = 0
                        rowSpec = GridLayout.spec(i / item.columns, 1f) // Распределение по строкам
                        columnSpec = GridLayout.spec(i % item.columns, 1f) // Распределение по столбцам
                    }

                    setImageBitmap(imageList[i].image)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    adjustViewBounds = true
                }
                gridLayout.addView(imageView)
            }
            delete_btn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val id = item.pageId
                    listener.onDeleteClick(id)
                }
            }
            edit_btn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onEditClick(PageWithImages(item, imageList))
                }
            }
        }
    }
}