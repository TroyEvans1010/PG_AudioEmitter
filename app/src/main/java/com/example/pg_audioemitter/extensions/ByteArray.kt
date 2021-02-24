package com.example.pg_audioemitter.extensions

import com.google.protobuf.ByteString

fun ByteArray.toLogStr() = this.map { it.toString() }.joinToString(",")
fun ByteArray.toByteString() = ByteString.copyFrom(this)