package com.example.texnostrelka_2025_otbor

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.texnostrelka_2025_otbor.adapters.ComicsAdapter
import com.example.texnostrelka_2025_otbor.adapters.ComicsNetworkAdapter
import com.example.texnostrelka_2025_otbor.database.PreferencesManager
import com.example.texnostrelka_2025_otbor.databinding.ActivityViewNetworkBinding
import com.example.texnostrelka_2025_otbor.factories.ViewNetworkViewModelFactory
import com.example.texnostrelka_2025_otbor.interfaces.OnItemClickListener
import com.example.texnostrelka_2025_otbor.repositories.NetworkRepository
import com.example.texnostrelka_2025_otbor.viewmodels.ViewNetworkViewModel

class ViewNetworkActivity : AppCompatActivity(), OnItemClickListener {
    private lateinit var binding: ActivityViewNetworkBinding
    private val viewModel: ViewNetworkViewModel by viewModels {
        ViewNetworkViewModelFactory(
            NetworkRepository(),
            PreferencesManager(this)
        )
    }
    private lateinit var comicsAdapter: ComicsNetworkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewNetworkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.RecyclerViewNetwork.layoutManager = LinearLayoutManager(this)
        binding.RecyclerViewNetwork.adapter = comicsAdapter
        viewModel.comics.observe( this, Observer { comics ->
            comicsAdapter.updateData(comics)
        })
        viewModel.fetchComics()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchComics()
    }

    override fun onItemClick(id: String) {
        val intent = Intent(this, ViewActivity::class.java)
        intent.putExtra("COMICS_ID", id)
        startActivity(intent)
    }

    override fun onDeleteClick(id: String) {
        TODO("Not yet implemented")
    }

    override fun onEditClick(id: String) {
        TODO("Not yet implemented")
    }
}