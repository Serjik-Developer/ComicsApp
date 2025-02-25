package com.example.texnostrelka_2025_otbor


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.adapters.komiksAdapter
import com.example.texnostrelka_2025_otbor.database.ComicsDatabase
import com.example.texnostrelka_2025_otbor.interfaces.OnItemClickListener
import com.example.texnostrelka_2025_otbor.models.ComicsModel
class MainActivity : AppCompatActivity(), OnItemClickListener {
    private lateinit var database: ComicsDatabase
    private lateinit var comics_list: MutableList<ComicsModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        database = ComicsDatabase(this)
        val add_btn = findViewById<Button>(R.id.btn_new)
        getData()
        add_btn.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }
    }

    override fun onItemClick(id: String) {
        val intent = Intent(this, ViewActivity::class.java)
        intent.putExtra("COMICS_ID", id)
        startActivity(intent)

    }
    override fun onDeleteClick(id: String) {
        database.delete(id)
        getData()
    }
    override fun onEditClick(id: String) {
        val intent= Intent(this, EditActivity::class.java)
        intent.putExtra("COMICS_ID", id)
        startActivity(intent)
    }
    fun getData() {
        comics_list = database.getAll()
        val recycler_view = findViewById<RecyclerView>(R.id.rv_komiks)
        recycler_view.layoutManager = LinearLayoutManager(this)
        val sharedPref = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        ComicsModel(sharedPref.javaClass.toString())
        val komiks_adaper = komiksAdapter(comics_list, this)
        recycler_view.adapter = komiks_adaper
    }
}