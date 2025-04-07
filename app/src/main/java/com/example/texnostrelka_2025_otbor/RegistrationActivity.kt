package com.example.texnostrelka_2025_otbor

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.texnostrelka_2025_otbor.database.PreferencesManager
import com.example.texnostrelka_2025_otbor.databinding.ActivityRegistrationBinding
import com.example.texnostrelka_2025_otbor.factories.RegistrationViewModelFactory
import com.example.texnostrelka_2025_otbor.repositories.NetworkRepository
import com.example.texnostrelka_2025_otbor.viewmodels.RegistrationViewModel

class RegistrationActivity : AppCompatActivity() {
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var binding: ActivityRegistrationBinding
    private val viewModel: RegistrationViewModel by viewModels {
        RegistrationViewModelFactory(
            NetworkRepository(),
            PreferencesManager(this)
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferencesManager = PreferencesManager(this)
        setupObservers()
        setupClickListeners()
    }
    private fun setupObservers() {
        viewModel.error.observe(this, Observer{ error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })
        viewModel.authSuccess.observe(this, Observer{ success ->
            if (success) {
                Toast.makeText(this, "Успешный вход как ${preferencesManager.getName()}", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        })
    }
    private fun setupClickListeners() {
        binding.toAuth.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
        }
        binding.RegMe.setOnClickListener {
            val login = binding.regLogin.text.toString()
            val password = binding.regPasword.text.toString()
            val name = binding.regName.text.toString()

            if (login.isBlank() || password.isBlank() || name.isBlank()) {
                Toast.makeText(this, "Заполните все формы", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.registrate(login, password, name)
        }
    }
}