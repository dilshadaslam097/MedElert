package com.bottech.medelert

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SharedPreferencesHelper(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE)

    fun saveLoginData(newCNIC: String, password: String, userId: Int) {
        val editor = sharedPreferences.edit()
        editor.putString("cnic", newCNIC)
        editor.putString("password", password)
        editor.putInt("userId", userId)
        editor.apply()
        Log.d("SharedPreferences", "Saved data:")
        Log.d("SharedPreferences", "  CNIC: $newCNIC")
        Log.d("SharedPreferences", "  Password: $password")
        Log.d("SharedPreferences", "  UserID: $userId")    }

    fun clearLoginData() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.contains("cnic") && sharedPreferences.contains("password")
    }

    fun getCnic(): String? {
        return sharedPreferences.getString("cnic", null)
    }

    fun getPassword(): String? {
        return sharedPreferences.getString("password", null)
    }
    fun getUserId(): Int? {
        return sharedPreferences.getInt("userId", -1)
    }

    fun setCnic() {
        return sharedPreferences.edit().putString("cnic", null).apply()
    }

    fun setPassword() {
        return sharedPreferences.edit().putString("password", null).apply()
    }
}