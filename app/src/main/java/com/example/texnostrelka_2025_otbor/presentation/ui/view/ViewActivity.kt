package com.example.texnostrelka_2025_otbor.presentation.ui.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.presentation.adapter.ViewAdapter
import com.example.texnostrelka_2025_otbor.domain.repository.ComicsRepository
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.texnostrelka_2025_otbor.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ViewActivity : AppCompatActivity() {
    @Inject lateinit var repository: ComicsRepository
    private val viewModel : ViewViewModel by viewModels()
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
        Log.w("COMICS_ID", comicsId)
        viewModel.pages.observe( this, Observer { pages ->
            val recyclerView = findViewById<RecyclerView>(R.id.RecyclerViewView)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = ViewAdapter(this, pages, repository, lifecycleScope)
        })
        viewModel.fetchData(comicsId)
    }

}