package com.example.pg_audioemitter.extensions

fun ByteArray.toSpecialStr(): String {
    return this.take(202).withIndex().map { (k, v) -> "$k:${v.toChar()}," }.joinToString("")
}

//fun ByteArray.toSpecialStr2(): String {
//    return this.take(202).withIndex().map { (k, v) -> "$k:${v.toInt().toBin}," }.joinToString("")
//}