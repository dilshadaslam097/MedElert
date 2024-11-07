package com.bottech.medelert.basics

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bottech.medelert.CNICTextWatcher
import com.bottech.medelert.R
import com.bottech.medelert.data.DatabaseHelper

class SignupActivity : AppCompatActivity() {

    private lateinit var signupCNIC: EditText
    private lateinit var signupName: EditText
    private lateinit var signupPassword: EditText
    private lateinit var signupAge: EditText
    private lateinit var signupDisease: EditText
    private lateinit var signupButton: Button
    private lateinit var loginRedirectText: TextView
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var togglePasswordVisibility: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        signupCNIC = findViewById(R.id.signupCNIC)
        signupName = findViewById(R.id.signupName)
        signupPassword = findViewById(R.id.signupPassword)
        signupAge = findViewById(R.id.signupAge)
        signupDisease = findViewById(R.id.signupDisease)
        signupButton = findViewById(R.id.signupButton)
        loginRedirectText = findViewById(R.id.loginRedirectText)
        togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility)
        databaseHelper = DatabaseHelper(this)

        signupCNIC.addTextChangedListener(CNICTextWatcher(signupCNIC))

        // Show/Hide password functionality
        signupPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        togglePasswordVisibility.setOnClickListener {
            val isPasswordVisible = signupPassword.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

            if (isPasswordVisible) {
                // Make password hidden
                signupPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_off)
            } else {
                // Make password visible
                signupPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_on)
            }

            // Move cursor to end after changing inputType
            signupPassword.setSelection(signupPassword.text.length)
        }

        signupButton.setOnClickListener {
            val trimmedName = signupName.text.toString().trim() // Remove leading/trailing spaces
            val cnic = signupCNIC.text.toString().replace("-", "")
            val disease = signupDisease.text.toString()
            val password = signupPassword.text.toString()

            var age = 0
            try {
                age = signupAge.text.toString().toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid Age format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate input according to specified constraints
            if (signupCNIC.length() < 15) {
                signupCNIC.error = "Invalid CNIC"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(trimmedName) || trimmedName.length > 15) {
                signupName.error = "Name must be between 1 and 15 characters"
                return@setOnClickListener
            }

            if (cnic.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "CNIC, Name & Password are mandatory", Toast.LENGTH_SHORT).show()
            } else if (password.length < 6 || password.length > 8) {
                Toast.makeText(this, "Password must be between 6 and 8 characters", Toast.LENGTH_SHORT).show()
            } else if (age < 0 || age > 99) {
                Toast.makeText(this, "Age must be between 0 and 99", Toast.LENGTH_SHORT).show()
            } else if (!disease.matches("[a-zA-Z ]+".toRegex()) || disease.length > 10) {
                Toast.makeText(this, "Disease should only contain letters (max 10 characters)", Toast.LENGTH_SHORT).show()
            } else {
                if (databaseHelper.checkCNIC(cnic)) {
                    Toast.makeText(this, "User already exists! Please login", Toast.LENGTH_SHORT).show()
                } else {
                    val insertSuccessful = databaseHelper.insertData(trimmedName, age, cnic, disease, password)
                    if (insertSuccessful) {
                        Toast.makeText(this, "Signup Successfully!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Signup Failed!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        loginRedirectText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
