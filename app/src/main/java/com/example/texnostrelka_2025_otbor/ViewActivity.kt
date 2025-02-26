package com.example.texnostrelka_2025_otbor

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.adapters.ViewAdapter
import com.example.texnostrelka_2025_otbor.database.ComicsDatabase
import com.example.texnostrelka_2025_otbor.models.Page

class ViewActivity : AppCompatActivity() {
    private lateinit var database: ComicsDatabase
    private lateinit var pageList: MutableList<Page>
    private lateinit var comicsId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        database = ComicsDatabase(this)
        comicsId = intent.getStringExtra("COMICS_ID")!!
        if (comicsId.isNotEmpty()) AppData.comicsId = comicsId
        val recyclerView = findViewById<RecyclerView>(R.id.RecyclerViewView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        pageList = database.getAllPages(comicsId)
        val pageAdapter = ViewAdapter(this, pageList)
        recyclerView.adapter = pageAdapter

    }

}