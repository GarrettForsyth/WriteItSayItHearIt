package com.example.android.sparkstories.ui.util

import android.text.Editable
import android.text.TextWatcher

open class TextWatcherAdapter: TextWatcher {
    override fun afterTextChanged(text: Editable?) {
        // no op
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        // no op
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        // no op
    }
}