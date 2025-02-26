package com.example.texnostrelka_2025_otbor.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Comics.db"
        private const val DATABASE_VERSION = 4

        private const val CREATE_TABLE_COMICS = """
            CREATE TABLE comics (
                id TEXT PRIMARY KEY,
                text TEXT,
                description TEXT,
                image BLOB NOT NULL
            )
        """
        private const val CREATE_TABLE_PAGES = """
            CREATE TABLE pages (
                pageId TEXT PRIMARY KEY,
                comicsId TEXT,
                number INTEGER NOT NULL,
                rows INTEGER NOT NULL,
                columns INTEGER NOT NULL
            )
        """
        private const val CREATE_TABLE_IMAGE = """
            CREATE TABLE image (
                id TEXT PRIMARY KEY,
                pageId TEXT,
                number INTEGER NOT NULL,
                page INTEGER NOT NULL,
                image BLOB NOT NULL
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_COMICS)
        db.execSQL(CREATE_TABLE_IMAGE)
        db.execSQL(CREATE_TABLE_PAGES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS comics")
        db.execSQL("DROP TABLE IF EXISTS image")
        db.execSQL("DROP TABLE IF EXISTS pages")
        onCreate(db)
    }
}
