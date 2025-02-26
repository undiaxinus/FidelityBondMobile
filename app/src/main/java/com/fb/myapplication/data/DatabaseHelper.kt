package com.fb.myapplication.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "FidelityBondDB"
        private const val DATABASE_VERSION = 1

        // User table
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_RANK = "rank"
        private const val COLUMN_UNIT = "unit"
        private const val COLUMN_FULL_NAME = "full_name"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_EMAIL TEXT UNIQUE NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_RANK TEXT NOT NULL,
                $COLUMN_UNIT TEXT NOT NULL,
                $COLUMN_FULL_NAME TEXT NOT NULL
            )
        """.trimIndent()
        
        db.execSQL(createTable)

        // Insert default admin user
        val adminValues = ContentValues().apply {
            put(COLUMN_EMAIL, "admin@example.com")
            put(COLUMN_PASSWORD, "admin123") // In production, this should be hashed
            put(COLUMN_RANK, "ADMIN")
            put(COLUMN_UNIT, "HQ")
            put(COLUMN_FULL_NAME, "System Administrator")
        }
        db.insert(TABLE_USERS, null, adminValues)

        // Insert some sample users
        val sampleUsers = arrayOf(
            arrayOf("john@army.com", "pass123", "PCPT", "NAGA CPO", "John Smith"),
            arrayOf("sarah@army.com", "pass456", "PMAJ", "ALBAY PPO", "Sarah Johnson"),
            arrayOf("mike@army.com", "pass789", "PLTCOL", "RHQ", "Mike Wilson")
        )

        sampleUsers.forEach { user ->
            val values = ContentValues().apply {
                put(COLUMN_EMAIL, user[0])
                put(COLUMN_PASSWORD, user[1])
                put(COLUMN_RANK, user[2])
                put(COLUMN_UNIT, user[3])
                put(COLUMN_FULL_NAME, user[4])
            }
            db.insert(TABLE_USERS, null, values)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun validateUser(email: String, password: String): UserData? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID, COLUMN_EMAIL, COLUMN_RANK, COLUMN_UNIT, COLUMN_FULL_NAME),
            "$COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(email, password),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            UserData(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                rank = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RANK)),
                unit = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNIT)),
                fullName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME))
            )
        } else {
            null
        }.also {
            cursor.close()
        }
    }
}

data class UserData(
    val id: Int,
    val username: String,
    val rank: String,
    val unit: String,
    val fullName: String
) 