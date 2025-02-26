package com.example.texnostrelka_2025_otbor

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
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

class EditActivity : AppCompatActivity(), OnItemPageClickListener {
    private lateinit var database: ComicsDatabase
    private lateinit var page_list: MutableList<Page>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = ComicsDatabase(this) // Инициализация базы данных

        val comicsId = intent.getStringExtra("COMICS_ID")!!
        val add_page = findViewById<Button>(R.id.button)
        val recyclerView = findViewById<RecyclerView>(R.id.RecyclerViewPages)
        recyclerView.layoutManager = LinearLayoutManager(this)

        if (comicsId != "new") {
            page_list = database.getAllPages(comicsId)
        } else {
            page_list = mutableListOf() // Инициализация пустого списка, если comicsId == "new"
        }

        val page_adapter = PagesAdapter(this, page_list, this) // Передаем this как OnItemPageClickListener
        recyclerView.adapter = page_adapter
        add_page.setOnClickListener {
            startActivity(Intent(this, EditPageActivity::class.java).putExtra("PAGE_WITH_IMAGES", "NEW"))
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onDeleteClick(pageId: String) {
        // Удаляем страницу из базы данных
        database.deletePage(pageId)
        // Обновляем список страниц
        page_list.removeAll { it.pageId == pageId }
        // Уведомляем адаптер об изменении данных
        findViewById<RecyclerView>(R.id.RecyclerViewPages).adapter?.notifyDataSetChanged()
    }

    override fun onEditClick(page: PageWithImages) {
        val intent = Intent(this, EditPageActivity::class.java).putExtra("PAGE_WITH_IMAGES", arrayOf(page))
        startActivity(intent)
    }
}