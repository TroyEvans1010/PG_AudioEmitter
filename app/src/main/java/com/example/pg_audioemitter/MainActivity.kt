package com.example.pg_audioemitter

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.pg_audioemitter.extensions.*
import com.example.pg_audioemitter.model_app.AudioEmitterResult
import com.example.pg_audioemitter.model_app.PartialAudioFormat
import com.tminus1010.tmcommonkotlin.logz.logz
import com.tminus1010.tmcommonkotlin.misc.toast
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    val mediaRecorderHelper by lazy { MediaRecorderHelper() }
    val partialAudioFormat by lazy {
        PartialAudioFormat(
            sampleRate = 96000,
            encoding = AudioFormat.ENCODING_PCM_16BIT,
        )
    }
    val tempFile by lazy {
        File.createTempFile("rtyerty", "file", cacheDir)
            .apply { deleteOnExit() }
            .logx("ppp")
    }
    val tempFile2 by lazy {
        File.createTempFile("ityuir", "file", cacheDir)
                .apply { deleteOnExit() }
                .logx("ooo")
    }
    val audioEmitter by lazy { AudioEmitter(partialAudioFormat) }
    val playAudioUtil by lazy { PlayAudioUtil() }


    override fun onCreate(savedInstanceState: Bundle?) {
        logz("!*!*! START")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // # Setup Click Listeners
        btn_record_and_playback.setOnClickListener {
            if (doPermissions()) return@setOnClickListener
            toastAndLog("Record And Playback start")
            Observable.just(Unit)
                    .doOnNext { tempFile2.writeBytes(ByteArray(0)) }
                    .flatMap {
                        logz("Record start")
                        audioEmitter.recordObservable(2, TimeUnit.SECONDS)
                                .doOnNext {
                                    if (it is AudioEmitterResult.AudioChunk) {
//                                        logz("AudioChunk:${it.byteString.toDisplayStr()}")
                                        tempFile2.appendBytes(it.byteString.toByteArray())
                                    }
                                }
                                .filter { it is AudioEmitterResult.Done }
                                .doOnNext { logz("Record done") }
                    }
                    .flatMap {
                        logz("Playback start")
                        playAudioUtil.playBytesObservable(tempFile2, partialAudioFormat)
                                .doOnNext { logz("Playback done") }
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ toastAndLog("Record And Playback done") })
                    {
                        toast("Record And Playback Error")
                        Log.e("TMLog", "TM`Record And Playback Error:", it)
                    }
        }
        btn_record_and_playback_mp3.setOnClickListener {
            if (doPermissions()) return@setOnClickListener
            toastAndLog("Record And Playback MP3 start")
            Observable.just(Unit)
                    .flatMap {
                        logz("Record MP3 start")
                        mediaRecorderHelper.recordObservable(FileOutputStream(tempFile).fd, 2, TimeUnit.SECONDS)
                                .doOnNext { logz("Play MP3 done") }
                    }
                    .flatMap {
                        logz("Playback MP3 start")
                        playAudioUtil.playMP3Observable(tempFile)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ toastAndLog("Record And Playback MP3 done") })
                    {
                        toast("Record And Playback MP3 Error")
                        Log.e("TMLog", "TM`Record And Playback MP3 Error:", it)
                    }
        }
    }

    fun playbackAsMP3() {
        playAudioUtil.playMP3Observable(tempFile)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ toastAndLog("Play MP3 done") })
                {
                    toast("Play MP3 Error")
                    Log.e("TMLog", "TM`Play MP3 Error:", it)
                }
    }

    fun recordAsMP3() {
        if (doPermissions()) return
        mediaRecorderHelper.recordObservable(FileOutputStream(tempFile).fd, 2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    toastAndLog("Record MP3 done")
//                    btn_1_5.isEnabled = true
                })
                {
                    toast("Record MP3 Error")
                    Log.e("TMLog", "TM`Record MP3 Error:", it)
                }
    }

    fun hasMicrophone(): Boolean {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
    }

    // * very hacky..
    fun doPermissions(): Boolean {
        if(checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 200)
            return true
        }
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 201)
            return true
        }
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 202)
            return true
        }
        return false
    }
}
