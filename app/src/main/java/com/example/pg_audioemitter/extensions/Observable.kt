package com.example.pg_audioemitter.extensions

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

fun <T> Observable<T>.endWithItem(item: T) = this.concatWith(Observable.just(item))

fun <IN, OUT> Observable<IN>.emitIfTimeout(timespan: Long, timeUnit: TimeUnit, item: OUT): Observable<OUT> {
    return this
        .take(1)
        .map { false }
        .timeout(timespan, timeUnit, Observable.just(true))
        .filter { it }
        .map { item }
}

fun <T> Observable<T>.toBehaviorSubject(): BehaviorSubject<T> {
    return BehaviorSubject.create<T>()
        .also { bs ->
            // Direct subscription (ie: this.subscribe(behaviorSubject)) is ignored by behaviorSubject.value.
            this.subscribe(
                { bs.onNext(it) },
                { bs.onError(it) },
                { bs.onComplete() })
        }
}
