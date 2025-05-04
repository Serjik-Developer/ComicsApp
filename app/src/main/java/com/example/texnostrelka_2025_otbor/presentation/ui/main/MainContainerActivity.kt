package com.example.texnostrelka_2025_otbor.presentation.ui.main


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.texnostrelka_2025_otbor.MyFirebaseMessagingService
import com.example.texnostrelka_2025_otbor.MyFirebaseMessagingService.Companion.COMICS_ID
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.databinding.ActivityMainContainerBinding
import com.example.texnostrelka_2025_otbor.presentation.ui.infocomic.InfoComicActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.main.fragments.MainComicsFragment
import com.example.texnostrelka_2025_otbor.presentation.ui.main.fragments.MyComicsNetworkFragment
import com.example.texnostrelka_2025_otbor.presentation.ui.main.fragments.ViewNetworkFragment
import com.example.texnostrelka_2025_otbor.presentation.ui.settings.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainContainerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainContainerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIncomingIntent(intent)
        binding = ActivityMainContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        if (savedInstanceState == null) {
            binding.bottomNav.selectedItemId = R.id.nav_main
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_main -> {
                    loadMainComicsFragment()
                    true
                }
                R.id.nav_my_comics -> {
                    loadMyComicsNetworkFragment()
                    true
                }
                R.id.nav_view_network -> {
                    loadViewNetworkFragment()
                    true
                }
                R.id.nav_view_favorite -> {
                    loadViewNetworkFragment(true)
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun loadMainComicsFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MainComicsFragment())
            .commit()
    }

    private fun loadMyComicsNetworkFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MyComicsNetworkFragment())
            .commit()
    }

    private fun loadViewNetworkFragment(isFavoriteMode: Boolean = false) {
        val fragment = ViewNetworkFragment.newInstance(isFavoriteMode)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        val comicsId = intent?.getStringExtra(MyFirebaseMessagingService.COMICS_ID)
        Log.d("MainContainerActivity", "Received comicsId: $comicsId")
        comicsId?.let { id ->
            startActivity(
                Intent(this, InfoComicActivity::class.java).apply {
                    putExtra(MyFirebaseMessagingService.COMICS_ID, id)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            )
            finish()
        }
    }
}