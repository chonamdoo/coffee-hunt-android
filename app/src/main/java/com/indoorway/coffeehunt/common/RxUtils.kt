package com.indoorway.coffeehunt.common

import android.util.Log
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

fun <T> Observable<T>.slowdown(period: Long = 17, unit: TimeUnit = TimeUnit.MILLISECONDS) = sample(period, unit)

fun <T> Observable<T>.logEvents(prefix: String = "RX", onNextToo: Boolean = false) = this
        .doOnEach { if(onNextToo || !it.isOnNext()) Log.w("$prefix EVENT", it.toString()) }
        .doOnSubscribe { Log.w("$prefix EVENT", "SUB")  }
        .doOnDispose { Log.w("$prefix EVENT", "DIS")  }
