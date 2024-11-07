package com.bottech.medelert.basics

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bottech.medelert.CNICTextWatcher
import com.bottech.medelert.R
import com.bottech.medelert.data.DatabaseHelper

class FindPasswordFragment : DialogFragment() {
    private lateinit var findPassword: Button
    private lateinit var forgetCNIC: EditText
    private lateinit var forgetName: EditText
    private lateinit var showPassword: TextView
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.fragment_find_password, null)

            databaseHelper = DatabaseHelper(requireContext())

            findPassword = view.findViewById(R.id.findPassword)
            forgetCNIC = view.findViewById(R.id.forgetCNIC)
            forgetName = view.findViewById(R.id.forgetName)
            showPassword = view.findViewById(R.id.showPassword)

            forgetCNIC.addTextChangedListener(CNICTextWatcher(forgetCNIC))

            findPassword.setOnClickListener {
                val cnic = forgetCNIC.text.toString()
                val newCNIC = cnic.replace("-", "")
                val fullName = forgetName.text.toString()

                if (forgetCNIC.length() < 15) {
                    forgetCNIC.error = "Invalid CNIC"
                    return@setOnClickListener
                }
                if (cnic.isEmpty() || fullName.isEmpty()) {
                    Toast.makeText(requireContext(), "All fields are mandatory", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val isDataFound = databaseHelper.checkCNICAndName(newCNIC,fullName)
                if (isDataFound) {
                    val password = databaseHelper.getPasswordByCNIC(newCNIC)
                    Log.d("PasswordRetrieval", "Retrieved password: $password")
                    showPassword.text = "Your password is: $password"
                } else {
                    Log.d("PasswordRetrieval", "Data not found for CNIC: $newCNIC and Name: $fullName")
                    Toast.makeText(requireContext(), "Data not found", Toast.LENGTH_SHORT).show()
                }
            }

            builder.setView(view)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}