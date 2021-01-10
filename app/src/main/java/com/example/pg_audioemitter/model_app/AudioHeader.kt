package com.example.pg_audioemitter.model_app

import com.tminus1010.tmcommonkotlin.logz.logz
import org.junit.Assert.assertEquals
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class AudioHeader(
    val totalAudioLen: Long,
    val totalDataLen: Long,
    val longSampleRate: Long,
    val channels: Int = 1,
    val byteRate: Long
) {
    companion object {
        fun create(byteArray: ByteArray): AudioHeader {
            val totalDataLen: Long =
                ByteBuffer.allocateDirect(4)
                    .apply {
                        order(ByteOrder.BIG_ENDIAN)
                        put(byteArray[4])
                        put(byteArray[5])
                        put(byteArray[6])
                        put(byteArray[7])
                        flip()
                    }
                    .int
                    .toLong()
            val channels: Int = 1
//                ByteBuffer.allocateDirect(1)
//                    .apply {
//                        order(ByteOrder.BIG_ENDIAN)
//                        put(0x00.toByte())
//                        put(0x00.toByte())
//                        put(0x00.toByte())
//                        put(byteArray[22])
//                        flip()
//                    }
//                    .int
            val longSampleRate: Long =
                ByteBuffer.allocateDirect(4)
                    .apply {
                        order(ByteOrder.BIG_ENDIAN)
                        put(byteArray[24])
                        put(byteArray[25])
                        put(byteArray[26])
                        put(byteArray[27])
                        flip()
                    }
                    .int
                    .toLong()
            val byteRate: Long =
                ByteBuffer.allocateDirect(4)
                    .apply {
                        order(ByteOrder.BIG_ENDIAN)
                        put(byteArray[28])
                        put(byteArray[29])
                        put(byteArray[30])
                        put(byteArray[31])
                        flip()
                    }
                    .int
                    .toLong()
            val s = byteArray.take(44).map { it.toChar() }.joinToString("")
//            val s: String = "${byteArray[36].toChar()}${byteArray[37].toChar()}${byteArray[38].toChar()}${byteArray[39].toChar()}"
            logz(s)
            assertEquals('d', byteArray[36].toChar())
//            assert('d'.toByte() == byteArray[36])
//            assert('a'.toByte() == byteArray[37])
//            assert('t'.toByte() == byteArray[38])
//            assert('a'.toByte() == byteArray[39])
            val totalAudioLen: Long =
                ByteBuffer.allocateDirect(4)
                    .apply {
                        order(ByteOrder.BIG_ENDIAN)
                        put(byteArray[40])
                        put(byteArray[41])
                        put(byteArray[42])
                        put(byteArray[43])
                        flip()
                    }
                    .int
                    .toLong()
            return AudioHeader(
                channels = channels,
                totalDataLen = totalDataLen,
                longSampleRate = longSampleRate,
                byteRate = byteRate,
                totalAudioLen = totalAudioLen
            )
        }
    }
    fun getByteArray(): ByteArray {
        return ByteArray(44)
            .also {
                it[0]  = 'R'.toByte() // RIFF/WAVE header
                it[1]  = 'I'.toByte()
                it[2]  = 'F'.toByte()
                it[3]  = 'F'.toByte()
                it[4]  = (totalDataLen and 0xff).toByte()
                it[5]  = (totalDataLen shr 8 and 0xff).toByte()
                it[6]  = (totalDataLen shr 16 and 0xff).toByte()
                it[7]  = (totalDataLen shr 24 and 0xff).toByte()
                it[8]  = 'W'.toByte()
                it[9]  = 'A'.toByte()
                it[10] = 'V'.toByte()
                it[11] = 'E'.toByte()
                it[12] = 'f'.toByte() // 'fmt ' chunk
                it[13] = 'm'.toByte()
                it[14] = 't'.toByte()
                it[15] = ' '.toByte()
                it[16] = 16 // 4 bytes: size of 'fmt ' chunk
                it[17] = 0
                it[18] = 0
                it[19] = 0
                it[20] = 1 // format = 1
                it[21] = 0
                it[22] = channels.toByte()
                it[23] = 0
                it[24] = (longSampleRate and 0xff).toByte()
                it[25] = (longSampleRate shr 8 and 0xff).toByte()
                it[26] = (longSampleRate shr 16 and 0xff).toByte()
                it[27] = (longSampleRate shr 24 and 0xff).toByte()
                it[28] = (byteRate and 0xff).toByte()
                it[29] = (byteRate shr 8 and 0xff).toByte()
                it[30] = (byteRate shr 16 and 0xff).toByte()
                it[31] = (byteRate shr 24 and 0xff).toByte()
                it[32] = (2 * 16 / 8).toByte() // block align
                it[33] = 0
                it[34] = 16 // bits per sample // TODO("16 was just a guess")
                it[35] = 0
                it[36] = 'd'.toByte()
                it[37] = 'a'.toByte()
                it[38] = 't'.toByte()
                it[39] = 'a'.toByte()
                it[40] = (totalAudioLen and 0xff).toByte()
                it[41] = (totalAudioLen shr 8 and 0xff).toByte()
                it[42] = (totalAudioLen shr 16 and 0xff).toByte()
                it[43] = (totalAudioLen shr 24 and 0xff).toByte()
            }
    }
}