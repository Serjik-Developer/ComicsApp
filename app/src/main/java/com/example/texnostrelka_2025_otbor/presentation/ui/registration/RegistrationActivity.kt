package com.example.texnostrelka_2025_otbor.presentation.ui.registration

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.databinding.ActivityRegistrationBinding
import com.example.texnostrelka_2025_otbor.presentation.factory.RegistrationViewModelFactory
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import com.example.texnostrelka_2025_otbor.presentation.ui.auth.AuthActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.main.MainContainerActivity

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
                startActivity(Intent(this, MainContainerActivity::class.java))
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