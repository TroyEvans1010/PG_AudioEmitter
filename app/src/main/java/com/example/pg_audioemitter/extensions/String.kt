package com.example.pg_audioemitter.extensions

import com.google.protobuf.ByteString

fun String.toByteString(): ByteString {
    require(this.matches(Regex("^[0-9]*\$"))) { "String must only contain digits" }
    return this
        .map { it.toString().toByte() }
        .fold(ByteArray(0)) { acc, bytes -> acc + bytes }
        .let { ByteString.copyFrom(it) }
}