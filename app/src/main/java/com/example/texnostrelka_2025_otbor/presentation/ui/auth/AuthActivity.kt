package com.example.texnostrelka_2025_otbor.presentation.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.main.MainContainerActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.registration.RegistrationActivity
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.databinding.ActivityAuthBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    @Inject lateinit var preferencesManager: PreferencesManager
    private lateinit var binding: ActivityAuthBinding
    private val viewModel: AuthViewModel by viewModels ()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.authSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(
                    this,
                    "Успешный вход как ${preferencesManager.getName()}",
                    Toast.LENGTH_LONG
                ).show()
                startActivity(Intent(this, MainContainerActivity::class.java))
                finish()
            }
        }
    }

    private fun setupClickListeners() {
        binding.toRegistration.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        binding.LogMe.setOnClickListener {
            val login = binding.logLogin.text.toString()
            val password = binding.logPass.text.toString()

            if (login.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            viewModel.authenticate(login, password)
        }
    }
}