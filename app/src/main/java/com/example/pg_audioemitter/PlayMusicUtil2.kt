package com.example.pg_audioemitter

import android.media.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.*


class PlayMusicUtil2 {
    fun play(tempFile: File) {
        val minBufferSize: Int = AudioTrack.getMinBufferSize(
            FREQUENCY,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,FREQUENCY,
            AudioFormat.CHANNEL_OUT_MONO,AUDIO_FORMAT,
            minBufferSize,
            AudioTrack.MODE_STREAM
        )
        // * AudioTrack.MODE_STREAM means that play() simply opens the stream.
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