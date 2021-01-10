package com.example.pg_audioemitter

import android.media.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.*


class PlayMusicUtil2 {
    fun play(tempFile: File) {
        val minBufferSize =
            AudioTrack.getMinBufferSize(
                FREQUENCY,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
        val audioAttributes =
            AudioAttributes.Builder()
                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                .build()
        val audioFormat =
            AudioFormat.Builder()
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .setSampleRate(FREQUENCY)
                .setEncoding(AUDIO_FORMAT)
                .build()
        val audioTrack =
            AudioTrack.Builder()
                .setAudioFormat(audioFormat)
                .setAudioAttributes(audioAttributes)
                .setBufferSizeInBytes(minBufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()
        // * MODE_STREAM means that play() does not play, it simply opens the stream.
        audioTrack.play()

        val inputStream = FileInputStream(tempFile)
        val audioData = ByteArray(minBufferSize)
        var dataSize = 0
        while(dataSize != -1) {
            audioTrack.write(audioData,0, dataSize)
            dataSize = inputStream.read(audioData)
        }
    }

    fun playObservable(file: File): Observable<Unit> {
        return Observable.just(file)
            .observeOn(Schedulers.computation())
            .map { play(it) }
    }
}