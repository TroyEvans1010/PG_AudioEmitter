package com.example.pg_audioemitter.extensions

import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

fun <T> Observable<T>.endWithItem(item: T): Observable<T> =
    this
        .concatWith(Observable.just(item))

fun <IN, OUT> Observable<IN>.emitIfTimeout(timespan: Long, timeUnit: TimeUnit, item: OUT): Observable<OUT> =
    this
        .take(1)
        .map { false }
        .timeout(timespan, timeUnit, Observable.just(true))
        .filter { it }
        .map { item }