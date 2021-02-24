package com.example.pg_audioemitter.rx_transformers

import com.google.protobuf.ByteString
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * This transformer emits a normalized value between 0 and 1 to represent how loud the audio is.
 * It determines the min and max dynamically.
 */
fun volumeTransformer() = ObservableTransformer<ByteString, Double> { upstream ->
    upstream
        // # Regroup samples into a new time interval, and cast bytes to ints.
        .concatMapIterable { it }
        .map { it.toInt() }
        .buffer(75, TimeUnit.MILLISECONDS)
        // # Filter out invalid audio chunks. It is invalid if:
        //      > the size is too small
        //      > it starts with consecutive 0s, either because the mic was off, or because my current version of AudioRecord always starts with a few audio chunks of 0s.
        //      > Every once in a while, 1 audio chunk from a previous AudioRecord slips through, so always ignore the first audio chunk.
        .filter { it.size > 500 && !it.take(8).all { it==0 } }
        .skip(1)
        // # Audio chunk -> AverageVolume
        .map { it.fold(0.0) { acc, v -> acc + abs(v) } / it.size }
        // # AverageVolume -> AverageVolume^2
        // Squaring the average volume gives more weight to louder noises. It seems to look better this way.
        .map { it.pow(2) }
        // # AverageVolume^2 -> Normalization
        // Normalization is basically just squishing the value to be between 0 and 1, based on a min and max.
        // Perhaps the min and max should be hard-coded, but then it would have to be different for different microphones..
        // Instead, the min and max are dynamically determined, but then locked in shortly after they reach a margin of difference.
        .publish { upstreamAverages ->
            val lockMinMax = PublishSubject.create<Unit>()
            upstreamAverages.withLatestFrom(
                // ## Min
                upstreamAverages.scan(Double.MAX_VALUE) { acc: Double, v: Double -> min(acc, v) }.skip(1)
                    .distinctUntilChanged()
                    .takeUntil(lockMinMax.take(1).delay(1500, TimeUnit.MILLISECONDS)),
                // ## Max
                upstreamAverages.scan(0.0) { acc: Double, v: Double -> max(acc, v) }.skip(1)
                    .distinctUntilChanged()
                    .takeUntil(lockMinMax.take(1).delay(1500, TimeUnit.MILLISECONDS)))
                { currentValue: Double, min: Double, max: Double ->
                    // ## When a sufficient margin is achieved, lock min and max after 1.5s
                    if (max > 1.8 * min) lockMinMax.onNext(Unit)
                    // ## The max should always have at least some margin away from min.
                    val maxRedefined = max(max, min * 1.8)
                    // ## Normalize to between 0 and 1.
                    ((currentValue - min) / (maxRedefined - min))
                        .let { min(max(it, 0.0), 1.0) }
                }
        }
}