package com.example.pg_audioemitter.extensions

import java.io.File

fun File.toByteArray() = org.apache.commons.io.FileUtils.readFileToByteArray(this)