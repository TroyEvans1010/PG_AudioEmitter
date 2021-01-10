package com.example.pg_audioemitter

import android.media.*
import android.util.Log
import com.tminus1010.tmcommonkotlin.logz.logz
import io.reactivex.rxjava3.core.Observable
import java.io.*
import java.util.concurrent.TimeUnit


class AudioRecorderHelper(val cacheDir: File) {
    val encoding: Int = AudioFormat.ENCODING_PCM_16BIT
    val channel: Int = AudioFormat.CHANNEL_IN_MONO
    val sampleRate: Int = 16000
//    val AUDIO_FORMAT = AudioFormat.Builder()
//        .setEncoding(encoding)
//        .setSampleRate(sampleRate)
//        .setChannelMask(channel)
//        .build()
//        .describeContents()
    val AUDIO_FORMAT = encoding
    val FREQUENCY = sampleRate

    val fileName: String = "tyjyujfghfgh" //getTempFilename()

    var recording = false

    val tempFile2 by lazy {
        File.createTempFile("pioupuiopuiop", "file", cacheDir)
//            .apply { deleteOnExit() }
            .logx("ddd")
    }


    fun startRecording() {
        val CHANNELCONFIG: Int = AudioFormat.CHANNEL_IN_MONO
        val os: OutputStream = FileOutputStream(tempFile2)
        val bufferSize = AudioRecord.getMinBufferSize(FREQUENCY, CHANNELCONFIG, AUDIO_FORMAT)
        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            FREQUENCY,
            CHANNELCONFIG,
            AUDIO_FORMAT,
            bufferSize
        )
        val audioData = ByteArray(bufferSize)
        recording = true
        audioRecord.startRecording()
        Observable.just(Unit)
            .delay(2, TimeUnit.SECONDS)
            .subscribe { recording = false }
        var read = 0
        while (recording) {
            read = audioRecord.read(audioData, 0, bufferSize)
            if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                try {
                    os!!.write(audioData)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        try {
            os!!.close()
        } catch (io: IOException) {
            io.printStackTrace()
        }
        logz("Recording done")
    }

    fun playRecording() {
        try {
            val inputStream = FileInputStream(tempFile2)

            // sampleRateInHz: 44100
            val minBufferSize: Int = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
            val audioData = ByteArray(minBufferSize)

            val audioTrack: AudioTrack = AudioTrack(
                AudioManager.STREAM_MUSIC,FREQUENCY,
                AudioFormat.CHANNEL_OUT_MONO,AUDIO_FORMAT,
                minBufferSize,AudioTrack.MODE_STREAM
            )
            audioTrack.play()

            var i = 0
            while(i != -1) {
                audioTrack.write(audioData,0, i)
                i = inputStream.read(audioData)
            }

        } catch(e: FileNotFoundException) {
            Log.e("TMLog","File not found");
        } catch(e: IOException) {
            Log.e("TMLog","IO Exception");
        }
        logz("Playback done")
    }
}