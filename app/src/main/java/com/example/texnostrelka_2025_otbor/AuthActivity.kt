package com.example.texnostrelka_2025_otbor

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.texnostrelka_2025_otbor.database.PreferencesManager
import com.example.texnostrelka_2025_otbor.databinding.ActivityAuthBinding
import com.example.texnostrelka_2025_otbor.factories.AuthViewModelFactory
import com.example.texnostrelka_2025_otbor.repositories.NetworkRepository
import com.example.texnostrelka_2025_otbor.viewmodels.AuthViewModel

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(
            NetworkRepository(),
            PreferencesManager(this)
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.LogMe.setOnClickListener {
            val login = binding.logLogin.text.toString()
            val password = binding.logPass.text.toString()
            if (login.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Заполните все формы", Toast.LENGTH_LONG).show()

                return@setOnClickListener
            }
            viewModel.authenticate(login, password) { result ->
                runOnUiThread {
                    when {
                        result.isSuccess -> {
                            Toast.makeText(this, "Успешный вход как ${result.getOrNull()?.name}", Toast.LENGTH_LONG).show()

                        }
                        result.isFailure -> {
                            Toast.makeText(
                                this, "Error ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }
}