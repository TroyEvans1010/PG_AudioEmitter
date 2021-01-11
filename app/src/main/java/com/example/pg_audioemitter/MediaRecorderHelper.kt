package com.example.pg_audioemitter

import android.media.MediaRecorder
import io.reactivex.rxjava3.core.Observable
import java.io.FileDescriptor
import java.util.concurrent.TimeUnit

class MediaRecorderHelper {
    fun recordObservable(outputFileDescriptor: FileDescriptor, long: Long, timeUnit: TimeUnit): Observable<Unit> {
        val mediaRecorder = MediaRecorder()
        return Observable.just(Unit)
            .doOnNext {
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                mediaRecorder.setOutputFile(outputFileDescriptor)

                mediaRecorder.prepare()
                mediaRecorder.start()
            }
            .delay(long, timeUnit)
            .doOnNext {
                mediaRecorder.stop()
                mediaRecorder.reset()
                mediaRecorder.release()
            }
    }
}