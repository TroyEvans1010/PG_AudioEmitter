package com.example.pg_audioemitter.extensions

import android.util.Log
import com.tminus1010.tmcommonkotlin.logz.logz
import io.reactivex.rxjava3.core.Observable

fun <T> Observable<T>.logc(prefix: Any?): Observable<T> {
    return this
        .doOnNext { logz("$prefix`$it") }
        .doOnError { Log.d("TMLog","TM`Failure:", it) }
}