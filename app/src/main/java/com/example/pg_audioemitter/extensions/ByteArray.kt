package com.example.pg_audioemitter.extensions

import com.google.protobuf.ByteString
import java.io.File
import java.io.FileOutputStream
import java.util.*

fun ByteArray.toLogStr() = this.map { it.toString() }.joinToString(",")
fun ByteArray.toByteString() = ByteString.copyFrom(this)
fun ByteArray.toTempFile(cacheDir: File): File =
    File.createTempFile(UUID.randomUUID().toString().take(15), ".file", cacheDir)
        .apply { deleteOnExit() }
        .also {
            FileOutputStream(it)
                .apply { write(this@toTempFile) }
                .apply { close() }
        }