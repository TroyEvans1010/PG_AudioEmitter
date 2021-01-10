package com.example.pg_audioemitter.extensions

@ExperimentalUnsignedTypes
fun Byte.toBitStr(): String {
    return this
        .toUByte()
        .toString(2)
        .let { "0".repeat(8 - it.length) + it }
}