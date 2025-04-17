package com.example.texnostrelka_2025_otbor.presentation.ui.editimagenetwork

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.texnostrelka_2025_otbor.databinding.ActivityEditNetworkBinding

class EditImageNetworkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditNetworkBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNetworkBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}