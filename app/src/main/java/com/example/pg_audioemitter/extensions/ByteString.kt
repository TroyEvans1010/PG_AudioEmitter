package com.example.pg_audioemitter.extensions

import com.google.protobuf.ByteString

fun ByteString.toLogStr(): String {
    return this.toByteArray().toLogStr()
}