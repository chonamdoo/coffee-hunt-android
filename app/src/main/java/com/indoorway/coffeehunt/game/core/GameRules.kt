package com.indoorway.coffeehunt.game.core

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.Subject
import com.indoorway.coffeehunt.game.core.Game.Player

const val EATING_DISTANCE = 1.0
const val KILLING_DISTANCE = 1.0
const val REGULAR_SEED_POINTS = 1
const val SPECIAL_SEED_POINTS = 10

fun Subject<Game.State>.applyAllGameRules(phases: Subject<Game.Phase>, pulse: Observable<Long>, userPositions: Observable<Position>, boards: Observable<Game.Board>) = apply {
    phases.subscribeWhenStarted(newGameRules(), this)
    phases.subscribeWhenStarted(newBoardRules(boards), this)
    phases.subscribeWhenStarted(userPositionRules(userPositions), this)
    phases.subscribeWhenStarted(pulseRules(pulse), this)
    phases.subscribeWhenStarted(finishGameRules(phases), this)
}

private fun Observable<Game.State>.newGameRules(): Observable<Game.State> =
        this.take(1).map { onNewBoard(it.board, it) }

private fun Observable<Game.State>.finishGameRules(phases: Subject<Game.Phase>): Observable<Game.State> = this
        .filter { it.player is Player.Existent.Dead }
        .doOnNext { phases.onNext(Game.Phase.FINISHED) }
        .filter { false }

private fun Observable<Game.State>.newBoardRules(boards: Observable<Game.Board>) =
        boards.withLatestFrom(this, BiFunction(::onNewBoard))

private fun onNewBoard(newBoard: Game.Board, oldGameState: Game.State) = newBoard.getNewGameState().copy(player = oldGameState.player.tryToResurrect())

private fun Observable<Game.State>.userPositionRules(userPositions: Observable<Position>) =
        userPositions.withLatestFrom(this, BiFunction(::onUpdatePlayerPosition))

fun onUpdatePlayerPosition(position: Position, state: Game.State): Game.State {
    val fullPosition = position.toFull(state.board.center)
    val seeds = state.seeds.filter { it.position.getDistanceTo(fullPosition) > EATING_DISTANCE }
    val score = Game.Score(state.player.score + state.seeds - seeds)
    val player = Player.Existent.Alive(fullPosition, score).tryToSurvive(state.monsters)
    return state.copy(player = player, seeds = seeds.toSet())
}

private fun Observable<Game.State>.pulseRules(pulse: Observable<Long>) =
        pulse.withLatestFrom(this, BiFunction(::onPulse))

fun onPulse(tick: Long, state: Game.State): Game.State {
    val monsters = state.monsters.map { it.move(state.board) }
    return state.copy(player = state.player.tryToSurvive(monsters), monsters = monsters)
}

private fun Player.tryToSurvive(monsters: Iterable<Game.Monster>) = when (this) {
    is Player.Existent.Alive -> tryToSurvive(monsters)
    else -> this
}

private fun Player.Existent.Alive.tryToSurvive(monsters: Iterable<Game.Monster>) =
        if (monsters.areKilling(this))
            Player.Existent.Dead(position, score)
        else
            Player.Existent.Alive(position, score)

private fun Iterable<Game.Monster>.areKilling(player: Player.Existent.Alive) = any { it.position.getDistanceTo(player.position) < KILLING_DISTANCE }

private fun Observable<Game.Phase>.subscribeWhenStarted(stateObservable: Observable<Game.State>, subject: Subject<Game.State>) {
    val startEvents = filter { it == Game.Phase.STARTED }
    val stopEvents = filter { it != Game.Phase.STARTED }
    startEvents
            .switchMap { stateObservable.takeUntil(stopEvents) }
            .doOnNext { subject.onNext(it) }
            .subscribe()
}
