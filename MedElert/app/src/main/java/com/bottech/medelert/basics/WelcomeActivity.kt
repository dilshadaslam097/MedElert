package com.bottech.medelert.basics

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
//import androidx.activity.EdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bottech.medelert.recordFragments.MainActivity
import com.bottech.medelert.R
import com.bottech.medelert.SharedPreferencesHelper

class WelcomeActivity : AppCompatActivity() {

    private lateinit var signupButton: Button
    private lateinit var loginButton: Button
    private lateinit var createNewAccount: TextView
    private lateinit var haveAccount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        EdgeToEdge.enable(this)
        setContentView(R.layout.activity_welcome)

        // Check if the user is already logged in
        val sharedPreferencesHelper = SharedPreferencesHelper(this)
        if (sharedPreferencesHelper.isLoggedIn()) {
            // User is logged in, navigate to the main screen
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } // else {
//            // User is not logged in, display the login screen
//            val intent = Intent(this, WelcomeActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//        if (sharedPreferencesHelper.getCnic() != null && sharedPreferencesHelper.getPassword() != null) {
//            // Proceed to the main activity or appropriate screen
//            Log.d("LoginData" , "cnic = " + sharedPreferencesHelper.getCnic())
//            Log.d("LoginData" , "password = " + sharedPreferencesHelper.getPassword())
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//        }

        signupButton = findViewById(R.id.signupButton)
        loginButton = findViewById(R.id.loginButton)
        createNewAccount = findViewById(R.id.createNewAccount)
        haveAccount = findViewById(R.id.haveAccount)

//        guestLogin = findViewById(R.id.guestButton)  // Uncomment if you want to use guest login

        createNewAccount.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        signupButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        haveAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

//        guestLogin.setOnClickListener {  // Uncomment if you want to use guest login
//            val intent = Intent(this, RemindersList::class.java)
//            startActivity(intent)
//        }

    }
}