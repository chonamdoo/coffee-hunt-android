package com.indoorway.coffeehunt.game.rules

import io.reactivex.Observable.never
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.junit.Assert.assertTrue
import org.junit.Test
import com.indoorway.coffeehunt.game.core.Game
import com.indoorway.coffeehunt.game.core.applyAllGameRules

class GameRulesTests {

    private val boards = PublishSubject.create<Game.Board>()
    private val phases = BehaviorSubject.createDefault(Game.Phase.IDLE).toSerialized()
    private val states = BehaviorSubject.createDefault(Game.EMPTY_STATE).toSerialized()
            .applyAllGameRules(phases, never(), never(), boards)
    private val statesTestObserver = states.test()

    init {
        phases.onNext(Game.Phase.STARTED)
        states.onNext(newState())
    }

    @Test
    fun shouldDisplayInitialState() {
        statesTestObserver.assertLastValueThat { this == newState() }
    }

    @Test
    fun shouldStartNewGameWhenNewPhaseStarted() {
        val state = newState(seeds = setOf(newSeed(0.0, 0.0)))
                .updatePlayerPosition(0.0, 0.0)
        states.onNext(state)
        statesTestObserver
                .lastValue()
                .assertPlayerScore(1)
        phases.onNext(Game.Phase.STARTED)
        statesTestObserver
                .lastValue()
                .assertPlayerScore(0)
    }

    @Test
    fun shouldResurrectPlayerOnNewGame() {
        val state = newState(monsters = listOf(newMonster()))
                .updatePlayerPosition(0.0, 0.0)
                .assertPlayerDead()
        states.onNext(state)
        phases.onNext(Game.Phase.STARTED)
        statesTestObserver
                .lastValue()
                .assertPlayerAlive()
                .assertPlayerPosition(0.0, 0.0)
    }

    @Test
    fun shouldResurrectPlayerOnNewBoard() {
        val state = newState(seeds = setOf(newSeed(0.0, 0.0)))
                .updatePlayerPosition(0.0, 0.0)
        states.onNext(state)
        statesTestObserver
                .lastValue()
                .assertPlayerScore(1)
        boards.onNext(newBoard())
        statesTestObserver
                .lastValue()
                .assertPlayerAlive()
                .assertPlayerScore(0)
    }
}

fun <T> TestObserver<T>.assertLastValueThat(condition: T.() -> Boolean) = assertTrue(condition(lastValue()))

private fun <T> TestObserver<T>.lastValue(): T = values().last()
