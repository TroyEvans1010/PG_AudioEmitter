package com.example.pg_audioemitter.extensions

import com.google.protobuf.ByteString

fun ByteArray.toLogStr(): String {
    return this.map { it.toString() }.joinToString(",")
}

fun ByteArray.toByteString(): ByteString {
    return ByteString.copyFrom(this)
}