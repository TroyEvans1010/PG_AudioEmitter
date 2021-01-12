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
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    val mediaRecorderHelper by lazy { MediaRecorderHelper() }
    val partialAudioFormat by lazy {
        PartialAudioFormat(
            sampleRate = 16000,
            encoding = AudioFormat.ENCODING_PCM_16BIT,
        )
    }
    val tempFile by lazy {
        File.createTempFile("rtyerty", "file", cacheDir)
            .apply { deleteOnExit() }
            .logx("ppp")
    }
    val audioEmitter by lazy { AudioEmitter(partialAudioFormat) }
    val playAudioUtil by lazy { PlayAudioUtil() }


    override fun onCreate(savedInstanceState: Bundle?) {
        logz("!*!*! START")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // # initialize view
        btn_2.isEnabled = false
        btn_1_5.isEnabled = false
        // # Setup Click Listeners
        btn_0.setOnClickListener {
            if (doPermissions()) return@setOnClickListener
            mediaRecorderHelper.recordObservable(FileOutputStream(tempFile).fd, 2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    toastAndLog("Record MP3 done")
                    btn_1_5.isEnabled = true
                })
                {
                    toast("Record MP3 Error")
                    Log.e("TMLog", "TM`Record MP3 Error:", it)
                }

        }
        btn_1.setOnClickListener {
            if (doPermissions()) return@setOnClickListener
            tempFile.writeBytes(ByteArray(0))
            audioEmitter.recordObservable(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it) {
                        is AudioEmitterResult.Done -> {
                            toastAndLog("Recording done")
                            btn_2.isEnabled = true
                        }
                        is AudioEmitterResult.AudioChunk -> {
                            tempFile.appendBytes(it.byteString.toByteArray())
                            logz("Audio Chunk:${it.byteString.toDisplayStr()}")
                        }
                    }
                })
                {
                    toast("Recording encountered error")
                    Log.e("TMLog", "TM`Recording encountered error:", it)
                }
        }
        btn_1_5.setOnClickListener {
            toastAndLog("Play MP3 start")
            playAudioUtil.playMP3Observable(tempFile)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ toastAndLog("Play MP3 done") })
                {
                    toast("Play MP3 Error")
                    Log.e("TMLog", "TM`Play MP3 Error:", it)
                }
        }
        btn_2.setOnClickListener {
            toastAndLog("Play Bytes start")
            playAudioUtil.playBytesObservable(tempFile, partialAudioFormat)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ toastAndLog("Play Bytes done") })
                {
                    toast("Play Bytes Error")
                    Log.e("TMLog", "TM`Play Bytes Error:", it)
                }
        }
        btn_3.setOnClickListener {
            toastAndLog("HasMic: ${hasMicrophone()}")
        }
        // # Print Something
        btn_4.setOnClickListener {
            // # Byte.toBitStr()
            17.toByte().toBitStr().logx("yyy")
            1010.toByte().toBitStr().logx("iii")
            64.toByte().toBitStr().logx("uuu")

            // # header
//            val byteArray = tempMp3.toByteArray()
//            val header = byteArray.take(44)
//            val headerPlus = byteArray.take(440)
//            val lastBit = byteArray.takeLast(44)
//            logz("header:$header")
//            logz("headerPlus:$headerPlus")
//            logz("lastBit:$lastBit")
        }
        btn_5.setOnClickListener {
            val byteArray = byteArrayOf(
                'd'.toByte(),
                'a'.toByte(),
                't'.toByte(),
                'a'.toByte()
            )
            tempFile.writeBytes(byteArray)
            Thread.sleep(1000)
            tempFile.toByteArray().toSpecialStr().logx("ttt")
        }
        btn_6.setOnClickListener {
            tempFile.toByteArray().toSpecialStr().logx("rrr")
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
