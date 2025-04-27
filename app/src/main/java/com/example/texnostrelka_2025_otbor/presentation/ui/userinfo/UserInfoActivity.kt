package com.example.texnostrelka_2025_otbor.presentation.ui.userinfo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.texnostrelka_2025_otbor.databinding.ActivityInfoUserBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}