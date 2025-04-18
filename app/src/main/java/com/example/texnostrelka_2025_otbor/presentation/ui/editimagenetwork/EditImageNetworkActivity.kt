package com.example.texnostrelka_2025_otbor.presentation.ui.editimagenetwork

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import com.example.texnostrelka_2025_otbor.databinding.ActivityEditImageNetworkBinding
import com.example.texnostrelka_2025_otbor.databinding.ActivityEditNetworkBinding
import com.example.texnostrelka_2025_otbor.presentation.factory.EditImageNetworkViewModelFactory

class EditImageNetworkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditImageNetworkBinding
    private val viewModel : EditImageNetworkViewModel by viewModels {
        EditImageNetworkViewModelFactory(NetworkRepository(), PreferencesManager(this))
    }
    private var pageId = ""
    private var imageId = ""
    private var mode = 0
    private var cellIndex = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditImageNetworkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mode = intent.getIntExtra("MODE", 0)
        when (mode) {
            0 -> throw IllegalArgumentException("Mode is required")
            1 -> {
                pageId = intent.getStringExtra("PAGE-ID")
                    ?: throw IllegalArgumentException("Pageid is required")
                cellIndex = intent.getIntExtra("CELL-INDEX", -1)
                if (cellIndex == -1) throw IllegalArgumentException("Cell Index is required")
            }
            2 -> imageId = intent.getStringExtra("IMAGE-ID") ?: throw IllegalArgumentException("ImageId is required")
        }
    }
}