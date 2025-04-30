package com.example.texnostrelka_2025_otbor.presentation.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.texnostrelka_2025_otbor.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}