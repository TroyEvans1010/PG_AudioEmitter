package com.example.pg_audioemitter.extensions

import android.content.Context
import android.widget.Toast
import com.tminus1010.tmcommonkotlin.logz.logz

fun Context.toastAndLog(msg: String, lengthID: Int = Toast.LENGTH_SHORT) {
    logz(msg)
    Toast.makeText(this, msg, lengthID).show()
}