package com.example.texnostrelka_2025_otbor

import android.os.Bundle
import android.preference.Preference
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.texnostrelka_2025_otbor.database.PreferencesManager
import com.example.texnostrelka_2025_otbor.databinding.ActivityRegistrationBinding
import com.example.texnostrelka_2025_otbor.factories.RegistrationViewModelFactory
import com.example.texnostrelka_2025_otbor.repositories.NetworkRepository
import com.example.texnostrelka_2025_otbor.viewmodels.RegistrationViewModel

class RegistrationActivity : AppCompatActivity() {
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
        binding.RegMe.setOnClickListener {
            val login = binding.regLogin.text.toString()
            val password = binding.regPasword.text.toString()
            val name = binding.regName.text.toString()

            if (login.isBlank() || password.isBlank() || name.isBlank()) {
                Toast.makeText(this, "Заполните все формы", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.registrate(login, password, name) { result ->
             runOnUiThread {
                 when {
                     result.isSuccess -> {
                         Toast.makeText(this, "Успешный вход как ${result.getOrNull()?.login}", Toast.LENGTH_LONG).show()
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