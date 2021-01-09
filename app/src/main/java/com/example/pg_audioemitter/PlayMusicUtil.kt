package com.example.pg_audioemitter

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class PlayMusicUtil {
    private val mediaPlayer = MediaPlayer()
    fun playMP3ByteArray(activity: AppCompatActivity, mp3SoundByteArray: ByteArray) {
        playMP3ByteArray(mp3SoundByteArray, activity.cacheDir)
    }

    fun playMP3ByteArray(mp3SoundByteArray: ByteArray, cacheDir: File) {
        // # Create temp file
        val tempMp3 = File.createTempFile("kurchina", "mp3", cacheDir)
            .apply { deleteOnExit() }
        // # Write to temp file
        FileOutputStream(tempMp3)
            .apply { write(mp3SoundByteArray) }
            .apply { close() }

        // # MediaPlayer is finicky. Reset it to be sure that everything works.
        // * If you're not using the main thread, you might need to create another MediaPlayer on that thread.
        mediaPlayer.reset()
        // # Forward results to mediaPlayerCompleted
//        mediaPlayer.setOnCompletionListener { mediaPlayerResults.onNext(Unit)}
//        mediaPlayer.setOnErrorListener { mp, what, extra -> mediaPlayerResults.onError(MediaPlayerException("what:$what extra:$extra")); true }

        // # Give temp file to MediaPlayer and play
        val fis = FileInputStream(tempMp3)
        // * If you pass directly, you might get: "Prepare failed.: status=0x1", so I'm using fd instead.
        mediaPlayer.setDataSource(fis.fd)
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

}