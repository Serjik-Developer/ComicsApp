package com.example.texnostrelka_2025_otbor

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.adapters.ViewAdapter
import com.example.texnostrelka_2025_otbor.database.ComicsDatabase
import com.example.texnostrelka_2025_otbor.factories.ViewViewModelFactory
import com.example.texnostrelka_2025_otbor.models.Page
import com.example.texnostrelka_2025_otbor.objects.AppData
import com.example.texnostrelka_2025_otbor.repositories.ComicsRepository
import com.example.texnostrelka_2025_otbor.viewmodels.ViewViewModel

class ViewActivity : AppCompatActivity() {
    private lateinit var viewModel: ViewViewModel
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
        comicsId = intent.getStringExtra("COMICS_ID") ?: throw IllegalArgumentException("COMICS_ID ARE REQUIRED")
        val database = ComicsDatabase(this)
        val repository = ComicsRepository(database)

        val factory = ViewViewModelFactory(comicsId, repository)
        viewModel = ViewModelProvider(this, factory).get(ViewViewModel::class.java)

        viewModel.pages.observe( this, Observer { pages ->
            val recyclerView = findViewById<RecyclerView>(R.id.RecyclerViewView)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = ViewAdapter(this, pages)

        })
    }

}