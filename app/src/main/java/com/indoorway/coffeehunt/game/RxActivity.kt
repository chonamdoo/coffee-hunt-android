package com.indoorway.coffeehunt.game

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject


open class RxActivity : AppCompatActivity() {

    enum class Lifecycle {
        NEW, CREATE, START, RESUME, PAUSE, STOP, DESTROY
    }

    private val lifecycleSubject = BehaviorSubject.createDefault(Lifecycle.NEW)

    val lifecycle: Observable<Lifecycle> = lifecycleSubject

    @CallSuper override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(Lifecycle.CREATE)
    }

    @CallSuper override fun onStart() {
        super.onStart()
        lifecycleSubject.onNext(Lifecycle.START)
    }

    @CallSuper override fun onResume() {
        super.onResume()
        lifecycleSubject.onNext(Lifecycle.RESUME)
    }

    @CallSuper override fun onPause() {
        lifecycleSubject.onNext(Lifecycle.PAUSE)
        super.onPause()
    }

    @CallSuper override fun onStop() {
        lifecycleSubject.onNext(Lifecycle.STOP)
        super.onStop()
    }

    @CallSuper override fun onDestroy() {
        lifecycleSubject.onNext(Lifecycle.DESTROY)
        super.onDestroy()
    }

    fun <T> Observable<T>.takeUntil(cycle: Lifecycle) = takeUntil(lifecycle.filter { it == cycle })
}

