package com.example.pg_audioemitter

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.pg_audioemitter.extensions.toastAndLog
import com.tminus1010.tmcommonkotlin.logz.logz
import com.tminus1010.tmcommonkotlin.misc.toast
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    val audioEmitter by lazy { AudioEmitter() }
    val playMusicUtil by lazy { PlayMusicUtil() }

    val tempMp3 by lazy {
        File.createTempFile("kurchina", "mp3", cacheDir)
            .apply { deleteOnExit() }.logx("aaa")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        logz("!*!*! START")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // # initialize view
        btn_2.isEnabled = false
        // # Setup Click Listeners
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
            MediaRecorderHelper().getRecordObservable(FileOutputStream(tempMp3).fd)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    logz("Successful recording complete")
                    toast("Successful recording complete")
                    btn_2.isEnabled = true
                })
                {
                    Log.d("TMLog", "TM`Recording encountered error:", it)
                    toast("Recording encountered error")
                }
        }
        btn_2.setOnClickListener {
            // # Play Audio
            playMusicUtil.playObservable(tempMp3)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ toastAndLog("Successful play complete")})
                { toastAndLog("play encountered error") }
        }
        btn_3.setOnClickListener {
            toast("HasMic: ${hasMicrophone()}")
        }
        btn_4.setOnClickListener {
            logz(FileInputStream(tempMp3).bufferedReader().readLine())
        }
    }

    fun hasMicrophone(): Boolean {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
    }
}