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
import androidx.lifecycle.Observer
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.databinding.ActivityRegistrationBinding
import com.example.texnostrelka_2025_otbor.presentation.ui.auth.AuthContainerActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.main.MainContainerActivity
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding : ActivityRegistrationBinding? = null
    private val binding get() = _binding!!
    private val viewModel : RegistrationViewModel by viewModels()
    @Inject lateinit var preferencesManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.error.observe(viewLifecycleOwner, Observer{ error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        })
        viewModel.authSuccess.observe(viewLifecycleOwner, Observer{ success ->
            if (success) {
                Toast.makeText(requireContext(), "Успешный вход как ${preferencesManager.getName()}", Toast.LENGTH_LONG).show()
                getAndSendFcmToken()
            }
        })
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
        binding.toAuth.setOnClickListener {
            (requireActivity() as AuthContainerActivity).loadAuthFragment()
        }
        binding.RegMe.setOnClickListener {
            val login = binding.regLogin.text.toString()
            val password = binding.regPasword.text.toString()
            val name = binding.regName.text.toString()

            if (login.isBlank() || password.isBlank() || name.isBlank()) {
                Toast.makeText(requireContext(), "Заполните все формы", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.registrate(login, password, name)
        }
    }
}