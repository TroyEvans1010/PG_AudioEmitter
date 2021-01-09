package com.example.pg_audioemitter

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.protobuf.ByteString
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val audioEmitter by lazy { AudioEmitter() }
    val playMusicUtil by lazy { PlayMusicUtil() }
    var collected = ByteString.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
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
            // # Start Recording
            audioEmitter.start {
                collected = collected.concat(it)
                collected.logx("aaa")
            }
        }
        btn_2.setOnClickListener {
            // # Play Audio
            playMusicUtil.playMP3ByteArray(this, collected.toByteArray())
        }
    }
}