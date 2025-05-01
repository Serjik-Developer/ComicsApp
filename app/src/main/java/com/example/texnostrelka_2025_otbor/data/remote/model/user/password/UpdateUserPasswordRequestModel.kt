package com.example.texnostrelka_2025_otbor.data.remote.model.user.password

data class UpdateUserPasswordRequestModel(
    val currentPassword: String,
    val newPassword: String
)