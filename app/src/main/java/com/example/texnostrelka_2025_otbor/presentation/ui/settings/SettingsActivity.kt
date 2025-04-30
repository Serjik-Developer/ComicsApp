package com.example.texnostrelka_2025_otbor.presentation.ui.settings

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.databinding.ActivitySettingsBinding
import com.example.texnostrelka_2025_otbor.presentation.utils.DialogHelper.showErrorDialog
import com.example.texnostrelka_2025_otbor.presentation.utils.base64ToBitmap
import com.example.texnostrelka_2025_otbor.presentation.utils.toBase64
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