package com.example.texnostrelka_2025_otbor.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Comic.db"
        private const val DATABASE_VERSION = 1

        private const val CREATE_TABLE_COMICS = """
            CREATE TABLE comics (
                id TEXT PRIMARY KEY,
                text TEXT,
                description TEXT,
                image TEXT
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_COMICS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS comics")
        onCreate(db)
    }
}
