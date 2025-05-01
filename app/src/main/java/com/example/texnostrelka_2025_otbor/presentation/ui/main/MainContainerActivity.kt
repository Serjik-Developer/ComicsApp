package com.example.texnostrelka_2025_otbor.presentation.ui.main


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.databinding.ActivityMainContainerBinding
import com.example.texnostrelka_2025_otbor.presentation.ui.main.fragments.MainComicsFragment
import com.example.texnostrelka_2025_otbor.presentation.ui.main.fragments.MyComicsNetworkFragment
import com.example.texnostrelka_2025_otbor.presentation.ui.main.fragments.ViewNetworkFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainContainerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainContainerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        binding.FavoriteComicsBtn.setOnClickListener {
            loadViewNetworkFragment(true)
        }
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
}