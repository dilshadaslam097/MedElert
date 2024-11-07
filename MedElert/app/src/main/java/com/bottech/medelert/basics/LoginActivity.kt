package com.bottech.medelert.basics

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.bottech.medelert.CNICTextWatcher
import com.bottech.medelert.recordFragments.MainActivity
import com.bottech.medelert.R
import com.bottech.medelert.SharedPreferencesHelper
import com.bottech.medelert.data.DatabaseHelper

class LoginActivity : AppCompatActivity() {

    private lateinit var loginCNIC: EditText
    private lateinit var loginPassword: EditText
    private lateinit var forgetPassword: TextView
    private lateinit var loginButton: Button
    private lateinit var signupRedirectText: TextView
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var fragmentManager: FragmentManager
    private var isFragmentVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Check if the user is already logged in
        val sharedPreferencesHelper = SharedPreferencesHelper(this)

        loginCNIC = findViewById(R.id.loginCNIC)
        loginPassword = findViewById(R.id.loginPassword)
        forgetPassword = findViewById(R.id.forgetPassword)
        loginButton = findViewById(R.id.loginButton)
        signupRedirectText = findViewById(R.id.signupRedirectText)
        databaseHelper = DatabaseHelper(this)

        fragmentManager = supportFragmentManager

        loginCNIC.addTextChangedListener(CNICTextWatcher(loginCNIC))

        loginButton.setOnClickListener {
            if (loginCNIC.length() < 15) {
                loginCNIC.error = "Invalid CNIC"
                return@setOnClickListener
            }

            val cnic = loginCNIC.text.toString()
            val newCNIC = cnic.replace("-", "")
            val password = loginPassword.text.toString()

            if (cnic.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show()
            } else {
                val isCNICValid = databaseHelper.checkCNIC(newCNIC)
                Log.d("LoginChecklogs","------- isCNICValid: $isCNICValid")
                val isPasswordCorrect = databaseHelper.checkPassword(newCNIC, password)
                Log.d("LoginChecklogs","------- isPasswordCorrect: $isPasswordCorrect")
                val userId = databaseHelper.getUserIdByCNIC(newCNIC)

                if (isCNICValid && isPasswordCorrect) {
                    Toast.makeText(this, "Login Successfully!", Toast.LENGTH_SHORT).show()
                    // Save login data to shared preferences
                    sharedPreferencesHelper.saveLoginData(cnic, password, userId)

                    // Proceed to the main activity or appropriate screen
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                } else if (!isCNICValid) {
                    Toast.makeText(this, "Invalid CNIC Number", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        forgetPassword.setOnClickListener {
            val fragment = FindPasswordFragment()
            fragment.show(supportFragmentManager, "findPasswordFragment")
        }

        signupRedirectText.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}