package com.example.pg_audioemitter.extensions

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.TimeUnit

/**
 * @param predicate Exposes the throwable so that you can return true to retry, or false to not retry.
 */
fun <T> Single<T>.retryWithDelay(delay: Long, timeUnit: TimeUnit, maxRetries: Int, predicate: ((Throwable) -> Boolean) = { true }): Single<T> {
    return this.retryWhen { upstreamExceptions ->
        var retryCount = 0
        upstreamExceptions.flatMap { throwable ->
            if (predicate(throwable) && retryCount++ < maxRetries)
                Flowable.timer(delay, timeUnit)
            else
                Flowable.error(throwable)
        }
    }
}

/**
 * Retry with delay1 until maxRetries1, then with delay2 until maxRetries2
 *
 * @param maxRetries1 How many times do you want to retry with delay1?
 * @param maxRetries2 How many times do you want to retry with delay2?
 * @param predicate Exposes the throwable so that you can return true to retry, or false to not retry.
 */
fun <T> Single<T>.retryWith2Delays(delay1: Long, timeUnit1: TimeUnit, delay2: Long, timeUnit2: TimeUnit, maxRetries1: Int = 1, maxRetries2:Int = 1, predicate: ((Throwable) -> Boolean) = { true }): Single<T> {
    return this.retryWhen { upstreamExceptions ->
        var retryCount1 = 0
        var retryCount2 = 0
        upstreamExceptions.flatMap { throwable ->
            val predicateResult = predicate(throwable)
            if (predicateResult && retryCount1++ < maxRetries1)
                Flowable.timer(delay1, timeUnit1)
            else if (predicateResult && retryCount2++ < maxRetries2)
                Flowable.timer(delay2, timeUnit2)
            else
                Flowable.error(throwable)
        }
    }
}