package com.indoorway.coffeehunt.game

import android.app.Application
import android.hardware.Camera
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import com.indoorway.coffeehunt.common.slowdown
import com.indoorway.coffeehunt.game.ar.HeadingEvents
import com.indoorway.coffeehunt.game.ar.PitchEvents
import com.indoorway.coffeehunt.game.camera.CameraWrapperImpl
import com.indoorway.coffeehunt.game.camera.LiveStream
import com.indoorway.coffeehunt.game.core.Game
import com.indoorway.coffeehunt.game.core.Game.EMPTY_STATE
import com.indoorway.coffeehunt.game.core.Position
import com.indoorway.coffeehunt.game.core.applyAllGameRules
import com.indoorway.coffeehunt.game.sensors.RxIndoorway
import com.indoorway.coffeehunt.game.sensors.getBoards
import com.indoorway.coffeehunt.game.sensors.getPitchEvents
import com.indoorway.coffeehunt.game.sensors.getPlayerPositions
import java.util.concurrent.TimeUnit

object DI {

    var provideApplicationContext: () -> Application = { throw UnsupportedOperationException("Initialize context in Application class") }

    val provideNewCameraWrapper: () -> LiveStream.CameraWrapper = { CameraWrapperImpl(provideApplicationContext(), Camera.open() ?: throw RuntimeException("No back-facing camera available")) }

    val provideUserPositions: () -> Observable<Position> = { getPlayerPositions() }

    val phases: Subject<Game.Phase> = BehaviorSubject.createDefault(Game.Phase.IDLE).toSerialized()

    val states: Observable<Game.State> by lazy {
        val pulse = Observable.interval(33, TimeUnit.MILLISECONDS).share()
        BehaviorSubject.createDefault(EMPTY_STATE).toSerialized()
                .applyAllGameRules(
                        phases,
                        pulse,
                        getPlayerPositions(),
                        getBoards())
    }

    val pitchEvents: PitchEvents by lazy { getPitchEvents(provideApplicationContext()).slowdown().share() }

    val headings: Observable<Float> by lazy { RxIndoorway.getHeadingObservable().slowdown() }

    val provideHeadingObservableInRadians: () -> HeadingEvents = { headings.map { Math.toRadians(it.toDouble()) } }
}