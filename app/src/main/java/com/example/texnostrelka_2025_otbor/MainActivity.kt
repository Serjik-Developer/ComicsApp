package com.example.texnostrelka_2025_otbor


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.adapters.ComiksAdapter
import com.example.texnostrelka_2025_otbor.database.ComicsDatabase
import com.example.texnostrelka_2025_otbor.interfaces.OnItemClickListener
import com.example.texnostrelka_2025_otbor.models.ComicsModel
import java.util.UUID

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
                val inputName = EditText(this)
                val inputDesc = EditText(this)
                inputName.hint = "Введите название комикса"
                inputDesc.hint = "Введите описание комикса"

                // Создаем контейнер для EditText
                val container = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(50, 20, 50, 20)
                    addView(inputName)
                    addView(inputDesc)
                }

                val dialog = AlertDialog.Builder(this)
                    .setTitle("Добавить комикс")
                    .setView(container) // Передаем контейнер с EditText
                    .setPositiveButton("Save") { _, _ ->
                        val desc = inputDesc.text.toString()
                        val name = inputName.text.toString()
                        if (desc.isNotEmpty() && name.isNotEmpty()) {
                            val comicsId = UUID.randomUUID().toString()
                            database.insert(comicsId, name, desc)
                            getData()

                        }
                    }
                    .setNegativeButton("Cancel") { _, _ -> }
                    .create()

                dialog.show()

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
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtra("COMICS_ID", id)
        startActivity(intent)
    }
    fun getData() {
        comics_list = database.getAll()
        val recycler_view = findViewById<RecyclerView>(R.id.rv_komiks)
        recycler_view.layoutManager = LinearLayoutManager(this)
        val komiks_adaper = ComiksAdapter(comics_list, this)
        recycler_view.adapter = komiks_adaper
    }
    override fun onResume() {
        super.onResume()
        getData() // Обновляем данные при каждом переходе на активность
    }

}