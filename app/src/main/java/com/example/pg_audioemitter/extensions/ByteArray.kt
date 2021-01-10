package com.example.pg_audioemitter.extensions

fun ByteArray.toSpecialStr(): String {
    return this.take(44).map { it.toChar() }.joinToString("")
}