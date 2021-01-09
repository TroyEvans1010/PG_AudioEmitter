package com.example.pg_audioemitter.extensions

import com.google.protobuf.ByteString

fun <T: ByteString> Iterable<T>.toByteString(): ByteString {
    return ByteString.copyFrom(this)
}