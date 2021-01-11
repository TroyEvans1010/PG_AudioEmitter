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
        audioTrack.stop()
        audioTrack.release()
    }


    /**
     * @param partialAudioFormat should be shared with Recorder object
     */
    fun playBytesObservable(file: File, partialAudioFormat: PartialAudioFormat): Observable<Unit> {
        return Observable.just(file)
            .observeOn(Schedulers.computation())
            .map { playBytes(it, partialAudioFormat) }
    }

    /**
     * This overload is not usually recommended because bytes can become excessively large, and
     * a lack of RAM will be an issue. Writing to and reading from a file is the standard pattern.
     *
     * @param partialAudioFormat should be shared with Recorder object
     */
    fun playBytesObservable(bytes: ByteArray, cacheDir: File, partialAudioFormat: PartialAudioFormat): Observable<Unit> {
        return File.createTempFile("playBytesObservable", "file", cacheDir)
            .apply { deleteOnExit() }
            .apply { writeBytes(bytes) }
            .let { playBytesObservable(it, partialAudioFormat) }
    }

    private val onCompletionSubject = PublishSubject.create<MediaPlayer>()

    fun playMP3Observable(file: File): Observable<Unit> {
        return Observable.just(Unit)
            .map {
                MediaPlayer()
                    .also { mediaPlayer ->
                        mediaPlayer.setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                                .build()
                        )
                        mediaPlayer.setDataSource(FileInputStream(file).fd)
                        mediaPlayer.setOnCompletionListener { onCompletionSubject.onNext(mediaPlayer) }
                        mediaPlayer.prepare()
                        mediaPlayer.start()
                    }
            }
            .flatMap { x -> onCompletionSubject.map { Pair(x, it) } }
            .filter { (a, b) -> a === b }
            .map { (a, _) -> a.release() }
    }
}