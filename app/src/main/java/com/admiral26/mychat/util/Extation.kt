package com.admiral26.mychat.util

import android.content.Context
import android.widget.Toast



fun Context.toast(value: String) {
    Toast.makeText(this, value, Toast.LENGTH_LONG).show()
}