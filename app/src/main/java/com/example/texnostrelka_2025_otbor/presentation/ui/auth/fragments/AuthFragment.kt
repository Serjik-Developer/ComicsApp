package com.example.texnostrelka_2025_otbor.presentation.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.databinding.ActivityAuthBinding
import com.example.texnostrelka_2025_otbor.presentation.ui.auth.AuthContainerActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.main.MainContainerActivity
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment : Fragment() {
    private var _binding : ActivityAuthBinding? = null
    private val binding get() = _binding!!
    private val viewModel : AuthViewModel by viewModels()
    @Inject lateinit var preferencesManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.authSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(
                    requireContext(),
                    "Успешный вход как ${preferencesManager.getName()}",
                    Toast.LENGTH_LONG
                ).show()
                getAndSendFcmToken()
            }
        }
    }

    private fun getAndSendFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FirebaseTokenCheck", "Token: $token")
                viewModel.postNotificationToken(token)
                startActivity(Intent(requireContext(), MainContainerActivity::class.java))
                activity?.finish()
            } else {
                Log.e("FirebaseTokenCheck", "Failed to get token", task.exception)
                startActivity(Intent(requireContext(), MainContainerActivity::class.java))
                activity?.finish()
            }
        }
    }

    private fun setupClickListeners() {
        binding.toRegistration.setOnClickListener {
            (requireActivity() as AuthContainerActivity).loadRegisterFragment()
        }

        binding.LogMe.setOnClickListener {
            val login = binding.logLogin.text.toString()
            val password = binding.logPass.text.toString()

            if (login.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            viewModel.authenticate(login, password)
        }
    }
}