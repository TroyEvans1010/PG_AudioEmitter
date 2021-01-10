package com.example.pg_audioemitter

import androidx.annotation.RequiresApi
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import com.example.pg_audioemitter.extensions.toByteString
import com.example.pg_audioemitter.model_app.AudioEmitterResult
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import com.google.protobuf.ByteString
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

/**
 * AudioEmitter feeds audio from the mic into a subscriber function.
 * It is from: https://github.com/GoogleCloudPlatform/android-docs-samples
 */
@RequiresApi(Build.VERSION_CODES.M) // AudioRecord requires SDK 23+
class AudioEmitter {
    val TAG = AudioEmitter::class.java.simpleName

    private var mAudioRecorder: AudioRecord? = null
    private var mAudioExecutor: ScheduledExecutorService? = null
    private lateinit var mBuffer: ByteArray

    fun recordObservable(long: Long, timeUnit: TimeUnit): Observable<AudioEmitterResult> {
        val audioChunkPublisher = PublishSubject.create<ByteString>()
        val arrayList = ArrayList<ByteString>()
        return Observable.merge(
            Observable.just(Unit)
                .doOnNext {
                    arrayList.clear() // is this necessary?
                    start { audioChunkPublisher.onNext(it) }
                }
                .delay(long, timeUnit)
                .doOnNext { stop() }
                .map { AudioEmitterResult.Done(arrayList.toByteString()) },
            audioChunkPublisher
                .doOnNext { arrayList.add(it) }
                .map { AudioEmitterResult.AudioChunk(it) }
        )
    }

    /** Start streaming  */
    fun start(
        encoding: Int = AudioFormat.ENCODING_PCM_16BIT,
        channel: Int = AudioFormat.CHANNEL_IN_MONO,
        sampleRate: Int = 16000,
        subscriber: (ByteString) -> Unit
    ) {
        mAudioExecutor = Executors.newSingleThreadScheduledExecutor()

        // create and configure recorder
        // Note: ensure settings are match the speech recognition config
        mAudioRecorder = AudioRecord.Builder()
            .setAudioSource(MediaRecorder.AudioSource.MIC)
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(encoding)
                    .setSampleRate(sampleRate)
                    .setChannelMask(channel)
                    .build()
            )
            .build()
        mBuffer = ByteArray(2 * AudioRecord.getMinBufferSize(sampleRate, channel, encoding))

        // start!
        mAudioRecorder!!.startRecording()

        // stream bytes as they become available in chunks equal to the buffer size
        mAudioExecutor!!.scheduleAtFixedRate({
            // read audio data
            val read = mAudioRecorder!!.read(mBuffer, 0, mBuffer.size, AudioRecord.READ_BLOCKING)

            // send next chunk
            if (read > 0) {
                subscriber(ByteString.copyFrom(mBuffer, 0, read))
            }
        }, 0, 10, TimeUnit.MILLISECONDS)
    }

    /** Stop Streaming  */
    fun stop() {
        // stop events
        mAudioExecutor?.shutdown()
        mAudioExecutor = null

        // stop recording
        mAudioRecorder?.stop()
        mAudioRecorder?.release()
        mAudioRecorder = null
    }
}