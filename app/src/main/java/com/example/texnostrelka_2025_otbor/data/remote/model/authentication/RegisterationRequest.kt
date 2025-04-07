package com.example.texnostrelka_2025_otbor.data.remote.model.authentication

data class RegisterationRequest(
    val login: String,
    val password: String,
    val name: String
)