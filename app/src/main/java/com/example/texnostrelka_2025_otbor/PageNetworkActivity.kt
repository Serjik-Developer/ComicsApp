package com.example.texnostrelka_2025_otbor

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PageNetworkActivity : AppCompatActivity() {
    private lateinit var PageAda
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_network)

        comicsId = intent.getStringExtra("COMICS_ID") ?: throw IllegalArgumentException("COMICS_ID ARE REQUIRED")
        Log.w("COMICS_ID", comicsId)
    }
}