package com.example.pg_audioemitter

import android.media.MediaRecorder
import io.reactivex.rxjava3.core.Observable
import java.io.FileDescriptor
import java.util.concurrent.TimeUnit

class MediaRecorderHelper(val mediaRecorder: MediaRecorder = MediaRecorder()) {
    fun getRecordObservable(outputFileDescriptor: FileDescriptor): Observable<Unit> {
        return Observable.just(Unit)
            .doOnNext {
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                mediaRecorder.setOutputFile(outputFileDescriptor)

                mediaRecorder.prepare()
                mediaRecorder.start()
            }
            .delay(2, TimeUnit.SECONDS)
            .doOnNext {
                mediaRecorder.stop()
                mediaRecorder.reset()
                mediaRecorder.release()
            }
    }
}