package com.example.pg_audioemitter

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pg_audioemitter.extensions.toByteString
import com.example.pg_audioemitter.extensions.toDisplayStr
import com.google.protobuf.ByteString
import com.tminus1010.tmcommonkotlin.logz.logz
import com.tminus1010.tmcommonkotlin_rx.observe
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    val audioEmitter by lazy { AudioEmitter() }
    val playMusicUtil by lazy { PlayMusicUtil() }
    var collected = arrayListOf<ByteString>()

    override fun onCreate(savedInstanceState: Bundle?) {
        logz("!*!*! START")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        btn_1.setOnClickListener {
            // # Permissions
            if(checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO),200)
                return@setOnClickListener
            }
            // # Permissions 2
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),201)
                return@setOnClickListener
            }
            // # Permissions 3
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),202)
                return@setOnClickListener
            }
            // # Start Recording
            audioEmitter.start {
                it.logx("aaa")
                collected.add(it)
            }
            // # Stop after some time
            Observable.just(Unit)
                .delay(2, TimeUnit.SECONDS)
                .observe(this) {
                    audioEmitter.stop()
                    logz("audioEmitter.stop()")
                    logz("collected:${collected.drop(1)}")
                    logz("ByteString.copyFrom(collected):${collected.drop(1).toByteString().toDisplayStr()}")
                }
        }
        btn_2.setOnClickListener {
            // # Play Audio
            playMusicUtil.playMP3ByteArray(this, collected.toByteString().toByteArray())
        }
    }
}