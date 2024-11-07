package com.bottech.medelert

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.bottech.medelert.data.DatabaseHelper

class UpdateProfileDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_update_profile, null)

        // SharedPreferences helper (assuming it exists)
        val sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        val databaseHelper = DatabaseHelper(requireContext()) // Assuming database helper exists

        // Get references to UI elements
        val userNameView = view.findViewById<TextView>(R.id.getUserCNIC)
        val updateName = view.findViewById<EditText>(R.id.updateName)
        val updateAge = view.findViewById<EditText>(R.id.updateAge)
        val updateDisease = view.findViewById<EditText>(R.id.updateDisease)
        val updateButton = view.findViewById<Button>(R.id.updateButton)

        // Retrieve CNIC from SharedPreferences (if available)
        val cnic = sharedPreferencesHelper.getCnic() ?: "" // Provide default value if null
        val newCNIC = cnic.replace("-", "")
        if (cnic != null) {
            userNameView.text = cnic  // Set retrieved CNIC to TextView (masking optional)
        } else {
            // Handle case where CNIC is not found (e.g., inform user)
            userNameView.text = "CNIC not available"
        }


        // Call function to fetch user data
        val userData = databaseHelper.getUserData(newCNIC)
        if (userData != null) {
            updateName.setText(userData.name)
            updateAge.setText(userData.age.toString()) // Convert age to string
            updateDisease.setText(userData.disease)
        } else {
            // Handle case where user data is not found (optional: show error message)
        }


        // Update profile button click listener
        updateButton.setOnClickListener {
            val name = updateName.text.toString().trim()
            val age = updateAge.text.toString().trim()
            val disease = updateDisease.text.toString().trim()

            // ... (basic input validation)

            val isProfileUpdated = databaseHelper.updateUserProfile(newCNIC, name, age, disease)

            if (isProfileUpdated) {
                // Update successful
                Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                // Optionally, dismiss the dialog or update UI in the parent activity
                dismiss()
            } else {
                // Update failed
                Toast.makeText(requireContext(), "Profile update failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setView(view)
        return builder.create()
    }
}