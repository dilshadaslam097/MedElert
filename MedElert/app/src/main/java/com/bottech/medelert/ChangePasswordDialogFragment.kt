package com.bottech.medelert

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bottech.medelert.data.DatabaseHelper
import com.bottech.medelert.recordFragments.MainActivity

class ChangePasswordDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_change_password, null)

        // SharedPreferences helper (assuming it exists)
        val sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        val databaseHelper = DatabaseHelper(requireContext())

        // Get references to the EditText and Button
        val userCNIC = view.findViewById<TextView>(R.id.getUserCNIC)
        val oldPassword = view.findViewById<EditText>(R.id.previousPassword)
        val newPassword = view.findViewById<EditText>(R.id.updateNewPassword)
        val confirmPassword = view.findViewById<EditText>(R.id.updateConfirmPassword)
        val updateButton = view.findViewById<Button>(R.id.updateButton)
        val togglePasswordVisibility = view.findViewById<ImageView>(R.id.togglePasswordVisibility)

        // Retrieve CNIC from SharedPreferences (if available)
        val cnic = sharedPreferencesHelper.getCnic()
        if (cnic != null) {
            userCNIC.text = cnic  // Set retrieved CNIC to TextView (masking optional)
        } else {
            // Handle case where CNIC is not found (e.g., inform user)
            userCNIC.text = "CNIC not available"
        }

        // Show/Hide password functionality
        newPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        togglePasswordVisibility.setOnClickListener {
            val isPasswordVisible = newPassword.inputType and InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

            if (isPasswordVisible) {
                // Make password hidden
                newPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_off)
            } else {
                // Make password visible
                newPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_on)
            }

            // Move cursor to end after changing inputType
            newPassword.setSelection(newPassword.text.length)
        }

        // Set up the button click listener
        updateButton.setOnClickListener {
            val cnic = userCNIC.text.toString()
            val newCNIC = cnic.replace("-", "")
            val oldPassword = oldPassword.text.toString()
            val newPassword = newPassword.text.toString()
            val confirmPassword = confirmPassword.text.toString()

            // Basic input validation
            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                // Show error message to the user
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(requireContext(), "New password and confirm password do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("ChangePassword", "Checking old password for CNIC: $newCNIC")

            val isPasswordCorrect = databaseHelper.checkPassword(newCNIC, oldPassword)

            if (!isPasswordCorrect) {
                Toast.makeText(requireContext(), "Incorrect old password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("ChangePassword", "Calling updatePassword with CNIC: $newCNIC, oldPassword: $oldPassword, newPassword: $newPassword")

            // Call the updatePassword function
            val isPasswordUpdated = databaseHelper.updatePassword(newCNIC, oldPassword, newPassword)
            Log.d("ChangePassword", "Status of isPasswordUpdated is: $isPasswordUpdated")

            if (isPasswordUpdated) {
                // Password updated successfully
                Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show()
                // Optionally, dismiss the dialog or navigate to a different screen
                dismiss()
            } else {
                Log.d("ChangePassword", "Password update failed. Checking databaseHelper.updatePassword return value.")
                // Password update failed
                Toast.makeText(requireContext(), "Password update failed. Please try again.", Toast.LENGTH_SHORT).show()
            }

        }

        builder.setView(view)
        return builder.create()
    }
}