package com.bottech.medelert.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "userDatabase.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create tables
        db.execSQL(
            """
        CREATE TABLE user_accounts (
          userId INTEGER PRIMARY KEY AUTOINCREMENT,
          userName TEXT NOT NULL,
          cnic TEXT NOT NULL,
          password TEXT NOT NULL,
          age INTEGER NOT NULL,
          disease TEXT,
          isUserCancelled BOOLEAN NOT NULL DEFAULT 0
        );
        """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades if needed
//        db.execSQL("DROP TABLE IF EXISTS user_accounts")
//        onCreate(db)
    }

    fun insertData(name: String, age: Int, cnic: String, disease: String?, password: String): Boolean {
        val db = writableDatabase
        val userAccountValues = ContentValues().apply{
            put("userName", name)
            put("age", age)
            put("cnic", cnic)
            put("disease", disease)
            put("password", password)
        }

        val result = db.insert("user_accounts", null, userAccountValues)
        return result != -1L
    }

    fun checkCNIC(cnic: String): Boolean {
        val db = readableDatabase
        val cursor = db.query("user_accounts", arrayOf(cnic), "cnic=?", arrayOf(cnic), null, null, null)
        return cursor.count > 0
    }

    @SuppressLint("Range")
    fun checkPassword(cnic: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            "user_accounts",
            arrayOf("password"), // Query for the password column
            "cnic=?",
            arrayOf(cnic),
            null,
            null,
            null
        )
        Log.d("DatabaseChecklogs","------- isPasswordCorrect: $password")
        if (cursor.moveToFirst()) {
            val storedPassword = cursor.getString(cursor.getColumnIndex("password")) // Access password column
            return storedPassword == password
        } else {
            return false
        }
    }

    fun checkCNICAndName(cnic: String, name: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            "user_accounts", arrayOf("*"), // Query all columns to ensure correct data retrieval
            "cnic=? AND userName=?", arrayOf(cnic, name), null, null, null
        )

        val result = cursor.count > 0
        cursor.close()
        return result
    }

    @SuppressLint("Range")
    fun getPasswordByCNIC(cnic: String): String {
        val db = readableDatabase
        val cursor = db.query("user_accounts", arrayOf("password"), "cnic=?", arrayOf(cnic), null, null, null)

        var password = ""
        if (cursor.moveToFirst()) {
            password = cursor.getString(cursor.getColumnIndex("password"))
        }
        cursor.close()
        return password
    }

    @SuppressLint("Range")
    fun getUserIdByCNIC(cnic: String): Int {
        val db = readableDatabase
        val cursor = db.query(
            "user_accounts", arrayOf("userId"), "cnic=?", arrayOf(cnic), null, null, null
        )

        var userId = -1  // Default value if not found

        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex("userId"))
        }

        cursor.close()
        return userId
    }

    @SuppressLint("Range")
    fun updatePassword(cnic: String, oldPassword: String, newPassword: String): Boolean {
        val db = writableDatabase
        var cursor: Cursor? = null
        Log.e("DatabaseError", "1")
        try {
            Log.d("DatabaseError", "2")
            cursor = db.query("user_accounts", arrayOf("password"), "cnic=?", arrayOf(cnic), null, null, null)

            Log.d("DatabaseError", "3")
            if (cursor.moveToFirst()) {
                Log.d("DatabaseError", "4")
                val storedPassword = cursor.getString(cursor.getColumnIndex("password"))
                if (storedPassword == oldPassword) {
                    Log.d("DatabaseError", "5")
                    val values = ContentValues()
                    values.put("password", newPassword) // Correctly update the password column
                    val rowsUpdated = db.update("user_accounts", values, "cnic=?", arrayOf(cnic))
                    Log.d("DatabaseError", "6")
                    return rowsUpdated > 0
                }
            }
        } catch (e: Exception) {
            Log.d("DatabaseError", "Error updating password: ${e.message}")
        } finally {
            cursor?.close()
        }
        return false
    }

    fun deleteAccount(cnic: String): Boolean {
        val db = writableDatabase

        try {
            val deletedRows = db.delete("user_accounts", "cnic=?", arrayOf(cnic))
            return deletedRows > 0
        } catch (e: Exception) {
            return false
        }
    }

    @SuppressLint("Range")
    fun getUserData(cnic: String): UserData? {
        val db = readableDatabase
        val cursor = db.query(
            "user_accounts", arrayOf("*"), // Query all columns
            "cnic=?", arrayOf(cnic), null, null, null
        )

        if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndex("userName"))
            val age = cursor.getInt(cursor.getColumnIndex("age"))
            val disease = cursor.getString(cursor.getColumnIndex("disease"))
            return UserData(name, age, disease)
        } else {
            return null
        }
    }
    // Data class to hold user information
    data class UserData(val name: String, val age: Int, val disease: String?)

    fun updateUserProfile(cnic: String, name: String, age: String, disease: String?): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("userName", name)
            put("age", age)
            put("disease", disease)
        }

        val rowsUpdated = db.update("user_accounts", values, "cnic=?", arrayOf(cnic))
        return rowsUpdated > 0
    }
}