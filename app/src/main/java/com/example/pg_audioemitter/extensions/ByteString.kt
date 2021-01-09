package com.example.pg_audioemitter.extensions

import com.google.protobuf.ByteString

fun ByteString.toDisplayStr(): String {
    return this.toByteArray().map { it.toString() }.joinToString(",")
}