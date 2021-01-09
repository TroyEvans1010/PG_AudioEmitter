package com.example.pg_audioemitter

import com.tminus1010.tmcommonkotlin.logz.logz

fun <T> T.logx(prefix:String): T {
    return this
        .also { logz("$prefix`$it") }
}