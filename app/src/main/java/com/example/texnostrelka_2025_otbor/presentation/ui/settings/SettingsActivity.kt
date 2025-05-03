package com.example.texnostrelka_2025_otbor.presentation.ui.settings

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.databinding.ActivitySettingsBinding
import com.example.texnostrelka_2025_otbor.presentation.ui.auth.AuthContainerActivity
import com.example.texnostrelka_2025_otbor.presentation.utils.DialogHelper.showErrorDialog
import com.example.texnostrelka_2025_otbor.presentation.utils.base64ToBitmap
import com.example.texnostrelka_2025_otbor.presentation.utils.toBase64
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModels()
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { loadAndConvertImage(it) }
    }
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonUploadAvatar.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        binding.buttonDeleteAvatar.setOnClickListener {
            viewModel.deleteUserAvatar()
        }
        binding.cardLogout.setOnClickListener {
            viewModel.logOut()
        }
        binding.cardChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }
        binding.cardChangeName.setOnClickListener {
            showChangeNameDialog()
        }
        viewModel.error.observe(this) { error ->
            error?.let {
                if (it == "Не авторизован.") {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AuthContainerActivity::class.java))
                }
                else {
                    showErrorDialog(error)
                }
            }
        }
        viewModel.postAvatarSuccess.observe(this) { isSuccess->
            if (isSuccess) {
                binding.imageViewAvatarSettings.setImageBitmap(bitmap)
                viewModel.resetPostAvatarSuccess()
            }
        }
        viewModel.deleteAvatarSuccess.observe(this) { isSuccess ->
            if(isSuccess) {
                binding.imageViewAvatarSettings.setImageResource(R.drawable.ic_avatar_placeholder)
                viewModel.resetDeleteAvatarSuccess()
            }
        }
        viewModel.userData.observe(this) { userInfo ->
            userInfo.avatar?.let {
                binding.imageViewAvatarSettings.setImageBitmap(it.base64ToBitmap())
            } ?: run {
                binding.imageViewAvatarSettings.setImageResource(R.drawable.ic_avatar_placeholder)
            }
        }
        viewModel.changeSuccess.observe(this) { isSuccess ->
            if(isSuccess) {
                Toast.makeText(this, "Успешно обновлено!", Toast.LENGTH_LONG).show()
                viewModel.resetChangeSuccess()
            }
        }
        viewModel.fetchUserData()
    }

    private fun showChangeNameDialog() {
        val inputNewName = EditText(this).apply {
            hint = "Введите новое имя пользователя"
            setTextAppearance(R.style.TextAppearance_MaterialComponents_Body1)
        }
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 20)
            addView(inputNewName)
        }
        val dialog = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_Rounded)
            .setTitle("Обновить пароль")
            .setView(container)
            .setPositiveButton("Сохранить") { _, _ ->
                val newName = inputNewName.text.toString()
                if (newName.isNotEmpty()) {
                    viewModel.updateName(newName)
                }
            }
            .setNegativeButton("Отмена") { _, _ -> }
            .create()
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            positiveButton.setTextAppearance(R.style.TextAppearance_MaterialComponents_Button)
            negativeButton.setTextAppearance(R.style.TextAppearance_MaterialComponents_Button)
        }
        dialog.show()
    }


    private fun showChangePasswordDialog() {
        val inputCurrentPassword = EditText(this).apply {
            hint = "Введите текущий пароль"
            setTextAppearance(R.style.TextAppearance_MaterialComponents_Body1)
        }

        val inputNewPassword = EditText(this).apply {
            hint = "Введите новый пароль"
            setTextAppearance(R.style.TextAppearance_MaterialComponents_Body1)
        }
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 20)
            addView(inputCurrentPassword)
            addView(inputNewPassword)
        }
        val dialog = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_Rounded)
            .setTitle("Обновить пароль")
            .setView(container)
            .setPositiveButton("Сохранить") { _, _ ->
                val newPassword = inputNewPassword.text.toString()
                val currentPassword = inputCurrentPassword.text.toString()
                if (newPassword.isNotEmpty() && currentPassword.isNotEmpty() && newPassword != currentPassword) {
                    viewModel.changePassword(currentPassword, newPassword)
                }
                else if(newPassword == currentPassword) {
                    Toast.makeText(this, "Пароли должны отличаться", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Отмена") { _, _ -> }
            .create()
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            positiveButton.setTextAppearance(R.style.TextAppearance_MaterialComponents_Button)
            negativeButton.setTextAppearance(R.style.TextAppearance_MaterialComponents_Button)
        }
        dialog.show()
    }

    private fun loadAndConvertImage(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            bitmap = Bitmap.createBitmap(BitmapFactory.decodeStream(inputStream))
            viewModel.postNewAvatar(bitmap.toBase64().toString())
        } catch (e: Exception) {
            e.printStackTrace()
            showErrorDialog(e.message.toString())
        }
    }
}