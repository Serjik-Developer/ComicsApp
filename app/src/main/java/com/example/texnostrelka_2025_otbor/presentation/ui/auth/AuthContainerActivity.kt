package com.example.texnostrelka_2025_otbor.presentation.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.texnostrelka_2025_otbor.databinding.ActivityAuthContainerBinding
import com.example.texnostrelka_2025_otbor.presentation.ui.auth.fragments.AuthFragment
import com.example.texnostrelka_2025_otbor.presentation.ui.auth.fragments.RegisterFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthContainerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthContainerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadAuthFragment()
    }

    fun loadAuthFragment() {
        supportFragmentManager.beginTransaction()
            .replace(binding.authFragment.id, AuthFragment())
            .commit()
    }

    fun loadRegisterFragment() {
        supportFragmentManager.beginTransaction()
            .replace(binding.authFragment.id, RegisterFragment())
            .commit()
    }
}