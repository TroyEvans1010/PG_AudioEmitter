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
    val audioEmitter by lazy { AudioEmitter(partialAudioFormat) }
    val playAudioUtil by lazy { PlayAudioUtil() }

    val tempFile by lazy {
        File.createTempFile("rtyerty", "file", cacheDir)
            .apply { deleteOnExit() }
            .logx("ppp")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        logz("!*!*! START")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // # initialize view
        btn_2.isEnabled = false
        // # Setup Click Listeners
        btn_0.setOnClickListener {
            // # Permissions
            if(checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 200)
                return@setOnClickListener
            }
            // # Permissions 2
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 201)
                return@setOnClickListener
            }
            // # Permissions 3
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 202)
                return@setOnClickListener
            }
            //
            mediaRecorderHelper.recordObservable(FileOutputStream(tempFile).fd, 3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    toastAndLog("Successful recording old-way complete")
                    btn_2.isEnabled = true
                })
                {
                    toast("Recording encountered error")
                    Log.e("TMLog", "TM`Recording encountered error:", it)
                }

        }
        btn_1.setOnClickListener {
            // # Permissions
            if(checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 200)
                return@setOnClickListener
            }
            // # Permissions 2
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 201)
                return@setOnClickListener
            }
            // # Permissions 3
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 202)
                return@setOnClickListener
            }
            //
            audioEmitter.recordObservable(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it) {
                        is AudioEmitterResult.Done -> {
                            toast("Recording done")
                            logz("Recording done. Combined:${it.combinedByteString.toDisplayStr()}")
                            tempFile.writeBytes(it.combinedByteString.toByteArray())
                            btn_2.isEnabled = true
                        }
                        is AudioEmitterResult.AudioChunk -> {
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
            // # Play Audio MP3
            playAudioUtil.playMP3Observable(tempFile)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ toastAndLog("Play Old Way done") })
                {
                    toast("Play Old Way encountered error")
                    Log.e("TMLog", "TM`Play Old Way encountered error:", it)
                }
        }
        btn_2.setOnClickListener {
            // # Play Audio Bytes
            playAudioUtil.playBytesObservable(tempFile, partialAudioFormat)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ toastAndLog("Play done") })
                {
                    toast("Play encountered error")
                    Log.e("TMLog", "TM`Play encountered error:", it)
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
}
