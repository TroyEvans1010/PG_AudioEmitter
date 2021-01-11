package com.example.pg_audioemitter

import android.media.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.io.*


class PlayAudioUtil {
    enum class Type { MP3, Bytes }

    fun playObservable(file: File, type: Type): Observable<Unit> {
        return when (type) {
            Type.MP3 -> playMP3Observable(file)
            Type.Bytes -> playBytesObservable(file)
        }
    }

    private fun playBytes(file: File) {
        val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        val FREQUENCY = 16000
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

        val inputStream = FileInputStream(file)
        val audioData = ByteArray(minBufferSize)
        var dataSize = 0
        while(dataSize != -1) {
            audioTrack.write(audioData,0, dataSize)
            dataSize = inputStream.read(audioData)
        }
        audioTrack.release()
    }

    private fun playBytesObservable(file: File): Observable<Unit> {
        return Observable.just(file)
            .observeOn(Schedulers.computation())
            .map { playBytes(it) }
    }

    private val onCompletionSubject = PublishSubject.create<MediaPlayer>()

    private fun playMP3Observable(file: File): Observable<Unit> {
        return Observable.just(file)
            .map {
                MediaPlayer()
                    .apply {
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                                .build()
                        )
                        setDataSource(FileInputStream(it).fd)
                        setOnCompletionListener { onCompletionSubject.onNext(this) }
                        prepare()
                        start()
                    }
            }
            .flatMap { x -> onCompletionSubject.map { Pair(x, it) } }
            .filter { (a, b) -> a === b }
            .map { (a, _) -> a.release() }
    }
}