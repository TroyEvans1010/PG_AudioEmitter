package com.example.pg_audioemitter

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import com.tminus1010.tmcommonkotlin.logz.logz
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream


class PlayMusicUtil {
    //    private val mediaPlayer = MediaPlayer()
    fun playMP3ByteArray(activity: AppCompatActivity, mp3SoundByteArray: ByteArray) {
        playMP3ByteArray(mp3SoundByteArray, activity.cacheDir)
    }

    fun playMP3ByteArray(mp3SoundByteArray: ByteArray, cacheDir: File) {
        // # Create temp file
        val tempMp3 = File.createTempFile("kurchina", "mp3", cacheDir)
            .apply { deleteOnExit() }.logx("aaa")
        // # Write to temp file
        FileOutputStream(tempMp3)
            .apply { write(mp3SoundByteArray) }
            .apply {
                WriteWaveFileHeader(
                    this,
                    1920,
                    1920,
                    16000,
                    1,
                    16000 * 2
                )
            }
            .apply { close() }

        initializeMediaPlayer(FileInputStream(tempMp3).fd)

        // # MediaPlayer is finicky. Reset it to be sure that everything works.
        // * If you're not using the main thread, you might need to create another MediaPlayer on that thread.
//        mediaPlayer.reset()
        // # Forward results to mediaPlayerCompleted
//        mediaPlayer.setOnCompletionListener { mediaPlayerResults.onNext(Unit)}
//        mediaPlayer.setOnErrorListener { mp, what, extra -> mediaPlayerResults.onError(MediaPlayerException("what:$what extra:$extra")); true }

        // # Give temp file to MediaPlayer and play
//        val fis = FileInputStream(tempMp3)
//        // * If you pass directly, you might get: "Prepare failed.: status=0x1", so I'm using fd instead.
//        mediaPlayer.setDataSource(fis.fd)
//        mediaPlayer.prepare()
//        mediaPlayer.start()
    }

    fun initializeMediaPlayer(fileDescriptor: FileDescriptor) {
        val mediaPlayer = MediaPlayer().logx("bbb")
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                .build()
        )
        mediaPlayer.setDataSource(fileDescriptor)
        mediaPlayer.prepare().logx("ccc")
        mediaPlayer.start().logx("ddd")
        mediaPlayer.setOnCompletionListener { logz("MediaPlayer completed audio") }
    }

    fun playObservable(byteArray: ByteArray, cacheDir: File): Observable<Unit> {
        // # Create temp file
        val tempMp3 = File.createTempFile("kurchina", "mp3", cacheDir)
            .apply { deleteOnExit() }.logx("aaa")
        // # Write to temp file
        FileOutputStream(tempMp3)
            .apply { write(byteArray) }
//            .apply {
//                WriteWaveFileHeader(
//                    this,
//                    1920,
//                    1920,
//                    16000,
//                    1,
//                    16000 * 2
//                )
//            }
            .apply { close() }

        return playObservable(tempMp3)
    }

    val onCompletionSubject = PublishSubject.create<MediaPlayer>()

    fun playObservable(file: File): Observable<Unit> {
        return Observable.just(file)
            .map {
                MediaPlayer()
                    .apply {
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                                .build()
                        )
                        setDataSource(FileInputStream(it).fd)
                        setOnCompletionListener { onCompletionSubject.onNext(this) }
                        prepare()
                        start()
                    }
            }
            .flatMap { x -> onCompletionSubject.map { Pair(x, it) } }
            .filter { (a, b) -> a === b }
            .map { (a, _) -> a.release() }
    }


    private fun initializeMediaPlayerAsync(fileDescriptor: FileDescriptor) {
        val mediaPlayer = MediaPlayer().logx("bbb")
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                .build()
        )
        mediaPlayer.setOnPreparedListener { it.start(); logz("mediaPlayer.start()") }.logx("ccc")
        mediaPlayer.setDataSource(fileDescriptor)
        mediaPlayer.prepareAsync().logx("ddd")
    }

    private fun WriteWaveFileHeader(
        out: FileOutputStream,
        totalAudioLen: Long,
        totalDataLen: Long,
        longSampleRate: Long,
        channels: Int,
        byteRate: Long
    ) {
        val header = ByteArray(44)
        header[0] = 'R'.toByte() // RIFF/WAVE header
        header[1] = 'I'.toByte()
        header[2] = 'F'.toByte()
        header[3] = 'F'.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = (totalDataLen shr 8 and 0xff).toByte()
        header[6] = (totalDataLen shr 16 and 0xff).toByte()
        header[7] = (totalDataLen shr 24 and 0xff).toByte()
        header[8] = 'W'.toByte()
        header[9] = 'A'.toByte()
        header[10] = 'V'.toByte()
        header[11] = 'E'.toByte()
        header[12] = 'f'.toByte() // 'fmt ' chunk
        header[13] = 'm'.toByte()
        header[14] = 't'.toByte()
        header[15] = ' '.toByte()
        header[16] = 16 // 4 bytes: size of 'fmt ' chunk
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1 // format = 1
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (longSampleRate and 0xff).toByte()
        header[25] = (longSampleRate shr 8 and 0xff).toByte()
        header[26] = (longSampleRate shr 16 and 0xff).toByte()
        header[27] = (longSampleRate shr 24 and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()
        header[32] = (2 * 16 / 8).toByte() // block align
        header[33] = 0
        header[34] = 16 // bits per sample // TODO("16 was just a guess")
        header[35] = 0
        header[36] = 'd'.toByte()
        header[37] = 'a'.toByte()
        header[38] = 't'.toByte()
        header[39] = 'a'.toByte()
        header[40] = (totalAudioLen and 0xff).toByte()
        header[41] = (totalAudioLen shr 8 and 0xff).toByte()
        header[42] = (totalAudioLen shr 16 and 0xff).toByte()
        header[43] = (totalAudioLen shr 24 and 0xff).toByte()
        out.write(header, 0, 44)
    }

}