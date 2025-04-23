package com.example.texnostrelka_2025_otbor.presentation.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DialogHelper {
    private fun showErrorDialog(context: Context, message: String) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Ошибка")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
    fun Fragment.showErrorDialog(message: String) {
        showErrorDialog(requireContext(), message)
    }
    fun AppCompatActivity.showErrorDialog(message: String) {
        showErrorDialog(this, message)
    }
}