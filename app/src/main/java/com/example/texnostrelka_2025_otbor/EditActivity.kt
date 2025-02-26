package com.example.texnostrelka_2025_otbor

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.adapters.PagesAdapter
import com.example.texnostrelka_2025_otbor.database.ComicsDatabase
import com.example.texnostrelka_2025_otbor.interfaces.OnItemPageClickListener
import com.example.texnostrelka_2025_otbor.models.Page
import com.example.texnostrelka_2025_otbor.models.PageWithImages
import com.example.texnostrelka_2025_otbor.models.PageWithImagesIds
import java.util.UUID

object AppData {
    var comicsId: String = ""
}

class EditActivity : AppCompatActivity(), OnItemPageClickListener {
    private lateinit var database: ComicsDatabase
    private lateinit var pageList: MutableList<Page>
    private lateinit var comicsId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        database = ComicsDatabase(this)
        comicsId = intent.getStringExtra("COMICS_ID")!!
        if (comicsId.isNotEmpty()) AppData.comicsId = comicsId
        val addPageButton = findViewById<Button>(R.id.button)
        val recyclerView = findViewById<RecyclerView>(R.id.RecyclerViewPages)
        recyclerView.layoutManager = LinearLayoutManager(this)

        pageList = database.getAllPages(comicsId)
        val pageAdapter = PagesAdapter(this, pageList, this)
        recyclerView.adapter = pageAdapter

        addPageButton.setOnClickListener {
            if (pageList.isEmpty()) {
                val pageId = UUID.randomUUID().toString()
                database.insertPage(pageId, comicsId, 1, 1, pageList.size)
                startActivity(
                    Intent(this, EditPageActivity::class.java)
                        .putExtra("ROWS_COUNT", 1)
                        .putExtra("COLUMNS_COUNT", 1)
                        .putExtra("PAGE_ID", pageId)
                )
            } else {
                val inputRows = EditText(this).apply { hint = "Введите количество ячеек в длину" }
                val inputColumns = EditText(this).apply { hint = "Введите количество ячеек в ширину" }
                AlertDialog.Builder(this)
                    .setTitle("Создать страницу")
                    .setView(LinearLayout(this).apply {
                        orientation = LinearLayout.VERTICAL
                        addView(inputRows)
                        addView(inputColumns)
                    })
                        .setPositiveButton("Сохранить") { _, _ ->
                            val rows = inputRows.text.toString().toIntOrNull() ?: 1
                            val columns = inputColumns.text.toString().toIntOrNull() ?: 1
                            val pageId = UUID.randomUUID().toString()
                            database.insertPage(pageId, comicsId, rows, columns, pageList.size)
                            startActivity(
                                Intent(this@EditActivity, EditPageActivity::class.java)
                                    .putExtra("ROWS_COUNT", rows)
                                    .putExtra("COLUMNS_COUNT", columns)
                                    .putExtra("PAGE_ID", pageId)
                            )
                        }
                        .setNegativeButton("Отмена", null)
                        .show()
            }
        }
    }

    override fun onDeleteClick(pageId: String) {
        database.deletePage(pageId)
        pageList.removeAll { it.pageId == pageId }
        findViewById<RecyclerView>(R.id.RecyclerViewPages).adapter?.notifyDataSetChanged()
    }

    override fun onEditClick(page: PageWithImages) {
        val imageIds = page.images.map { it.id!! } // Получаем список идентификаторов
        val pageWithImageIds = PageWithImagesIds(page.page, imageIds)

        startActivity(
            Intent(this, EditPageActivity::class.java)
                .putExtra("PAGE_WITH_IMAGES", pageWithImageIds)
        )
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        pageList = database.getAllPages(AppData.comicsId) // Обновляем данные
        Log.w("TEST-COMICS-ID", AppData.comicsId) // Логируем comicsId

        val adapter = findViewById<RecyclerView>(R.id.RecyclerViewPages).adapter as PagesAdapter
        adapter.updateData(pageList) // Обновляем данные в адаптере
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onStart() {
        super.onStart()
        pageList = database.getAllPages(AppData.comicsId) // Обновляем данные
        Log.w("TEST-COMICS-ID", AppData.comicsId) // Логируем comicsId

        val adapter = findViewById<RecyclerView>(R.id.RecyclerViewPages).adapter as PagesAdapter
        adapter.updateData(pageList) // Обновляем данные в адаптере
    }

}