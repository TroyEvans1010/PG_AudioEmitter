package com.example.pg_audioemitter

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.FileUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.pg_audioemitter.extensions.toByteArray
import com.example.pg_audioemitter.extensions.toDisplayStr
import com.example.pg_audioemitter.extensions.toastAndLog
import com.example.pg_audioemitter.model_app.AudioEmitterResult
import com.example.pg_audioemitter.model_app.AudioHeader
import com.tminus1010.tmcommonkotlin.logz.logz
import com.tminus1010.tmcommonkotlin.misc.toast
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    val mediaRecorderHelper by lazy { MediaRecorderHelper() }
    val audioEmitter by lazy { AudioEmitter() }
    val playMusicUtil by lazy { PlayMusicUtil() }

    val tempMp3 by lazy {
        File.createTempFile("kurchina", "mp3", cacheDir)
            .apply { deleteOnExit() }
            .logx("aaa")
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
            mediaRecorderHelper.recordObservable(FileOutputStream(tempMp3).fd)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    toastAndLog("Successful recording old-way complete")
//                    logz("AudioHeader:${AudioHeader.create(tempMp3.toByteArray())}")
                    btn_2.isEnabled = true
                })
                {
                    toast("Recording encountered error")
                    Log.e("TMLog","TM`Recording encountered error:", it)
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
                            toast("Successful recording complete")
                            logz("Successful recording complete. Combined:${it.combinedByteString.toDisplayStr()}")
                            tempMp3.writeBytes(it.combinedByteString.toByteArray())
                            val header = it.combinedByteString.toByteArray().take(44)
                            logz("header:$header")

                            // * I think this^ is not enough - it also needs a header, which idk how to make.
                            btn_2.isEnabled = true
                        }
                        is AudioEmitterResult.AudioChunk -> {
                            logz("Audio Chunk:${it.byteString.toDisplayStr()}")
                        }
                    }
                })
                {
                    toast("Recording encountered error")
                    Log.e("TMLog","TM`Recording encountered error:", it)
                }
        }
        btn_2.setOnClickListener {
            // # Play Audio
            playMusicUtil.playObservable(tempMp3)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ toastAndLog("Successful play complete")})
                {
                    toast("Play encountered error")
                    Log.e("TMLog","TM`Play encountered error:", it)
                }
        }
        btn_3.setOnClickListener {
            toastAndLog("HasMic: ${hasMicrophone()}")
        }
        btn_4.setOnClickListener {
            val byteArray = tempMp3.toByteArray()
            val header = byteArray.take(44)
            val headerPlus = byteArray.take(440)
            val lastBit = byteArray.takeLast(44)
            logz("header:$header")
            logz("headerPlus:$headerPlus")
            logz("lastBit:$lastBit")

//            logz(FileInputStream(tempMp3)  .bufferedReader().readLine())
//            logz(FileInputStream(tempMp3).bufferedReader().readLine())
        }
    }

    fun hasMicrophone(): Boolean {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
    }
}
