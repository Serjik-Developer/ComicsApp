package com.example.texnostrelka_2025_otbor.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.texnostrelka_2025_otbor.models.ImageModel
import com.example.texnostrelka_2025_otbor.models.komiks_main
import java.io.ByteArrayOutputStream
import java.util.UUID

class ComicsDatabase(context: Context) {

    private val databaseHelper = DatabaseHelper(context)

    fun insert(id:String, text: String, description: String, image: String) {
        val db = databaseHelper.writableDatabase

        val values = ContentValues().apply {
        put("id", id)
        put("text", text)
        put("description", description)
        put("image", image)
        }

        // insert the data into the table
        db.insert("comics", null, values)

        // close the database connection
        db.close()
    }

    @SuppressLint("Range")
    fun getAll(): MutableList<komiks_main> {
        val list = mutableListOf<komiks_main>()

        val db = databaseHelper.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM comics", null)

        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndex("id"))
            val name = cursor.getString(cursor.getColumnIndex("text"))
            val code = cursor.getString(cursor.getColumnIndex("description"))
            val image = cursor.getString(cursor.getColumnIndex("image"))
            list.add(komiks_main(id, name, code, image))
        }

    // close the cursor and database connection
        cursor.close()
        db.close()

        return list
    }

    fun update(id: String, text: String, description: String, image: String) {
        val db = databaseHelper.writableDatabase

        val values = ContentValues().apply {
            put("text", text)
            put("description", description)
            put("image", image)
        }

        // update the data in the table
        db.update("comics", values, "id = ?", arrayOf(id.toString()))

        // close the database connection
        db.close()
    }

    fun delete(id: String) {

        val db = databaseHelper.writableDatabase

        // delete the data from the table
        db.delete("comics", "id = ?", arrayOf(id))

        // close the database connection
        db.close()
    }
    fun insertPainting(bitmap: Bitmap, number:Int, comicsId: String) {
        val db = databaseHelper.writableDatabase
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val values = ContentValues().apply {
            put("id", UUID.randomUUID().toString())
            put("comicsId", comicsId)
            put("number", number)
            put("image", stream.toByteArray())
        }
        db.insert("image", null, values)
        db.close()
    }

    @SuppressLint("Range")
    fun getPainting(comicsId: String): List<ImageModel> {
        val db = databaseHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM image WHERE comicsId=$comicsId", null)

        val imageList = mutableListOf<ImageModel>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndex("id"))
                val comicsIdReturn = cursor.getString(cursor.getColumnIndex("comicsId"))
                val number = cursor.getInt(cursor.getColumnIndex("number"))
                val imageByteArray = cursor.getBlob(cursor.getColumnIndex("id"))
                val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
                val imageModel = ImageModel(id, comicsIdReturn, number, bitmap)
                imageList.add(imageModel)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return imageList
    }

}
