package com.example.pg_audioemitter

import android.media.*
import com.example.pg_audioemitter.extensions.getAudioTrackMinBufferSize
import com.example.pg_audioemitter.model_app.PartialAudioFormat
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.io.File
import java.io.FileInputStream


class PlayAudioUtil {
    private fun playBytes(file: File, partialAudioFormat: PartialAudioFormat) {
        val audioFormat =
            AudioFormat.Builder()
                .setEncoding(partialAudioFormat.encoding)
                .setSampleRate(partialAudioFormat.sampleRate)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()
        val audioAttributes =
            AudioAttributes.Builder()
                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                .build()
        val audioTrack =
            AudioTrack.Builder()
                .setAudioFormat(audioFormat)
                .setAudioAttributes(audioAttributes)
                .setBufferSizeInBytes(audioFormat.getAudioTrackMinBufferSize())
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()
        // * MODE_STREAM means that play() does not play, it simply opens the stream.
        audioTrack.play()

        val inputStream = FileInputStream(file)
        val audioData = ByteArray(audioFormat.getAudioTrackMinBufferSize())
        var dataSize = 0
        while(dataSize != -1) {
            audioTrack.write(audioData,0, dataSize)
            dataSize = inputStream.read(audioData)
        }
        audioTrack.release()
    }

    fun playBytesObservable(file: File, partialAudioFormat: PartialAudioFormat): Observable<Unit> {
        return Observable.just(file)
            .observeOn(Schedulers.computation())
            .map { playBytes(it, partialAudioFormat) }
    }

    private val onCompletionSubject = PublishSubject.create<MediaPlayer>()

    fun playMP3Observable(file: File): Observable<Unit> {
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