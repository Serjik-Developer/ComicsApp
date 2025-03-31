package com.example.texnostrelka_2025_otbor.models.authentication

data class RegisterationRequest(
    val login: String,
    val password: String,
    val name: String
)
