package com.bottech.medelert

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class CNICTextWatcher(private val editText: EditText) : TextWatcher {

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        s?.let {
            if (it.length > 0 && (it.length == 5 || it.length == 13)) {
                it.insert(it.length, "-")
            }
        }
    }
}