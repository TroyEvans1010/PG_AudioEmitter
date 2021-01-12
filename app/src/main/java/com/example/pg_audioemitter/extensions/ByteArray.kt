package com.example.pg_audioemitter.extensions

import com.google.protobuf.ByteString

fun ByteArray.toSpecialStr(): String {
    return this.take(202).withIndex().map { (k, v) -> "$k:${v.toChar()}," }.joinToString("")
}

//fun ByteArray.toSpecialStr2(): String {
//    return this.take(202).withIndex().map { (k, v) -> "$k:${v.toInt().toBin}," }.joinToString("")
//}

fun ByteArray.toDisplayStr(): String {
    return this.map { it.toString() }.joinToString(",")
}