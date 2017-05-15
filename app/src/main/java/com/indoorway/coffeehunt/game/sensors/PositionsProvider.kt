package com.indoorway.coffeehunt.game.sensors

import com.indoorway.android.common.sdk.model.Coordinates
import com.indoorway.android.common.sdk.model.IndoorwayNode
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import com.indoorway.coffeehunt.game.core.Game
import com.indoorway.coffeehunt.game.core.Position
import com.indoorway.coffeehunt.game.core.toFull

fun getPlayerPositions(): Observable<Position> = RxIndoorway.getUserPositionsObservable()
        .observeOn(Schedulers.computation())
        .map { it.coordinates.toPosition() }

fun getBoards(): Observable<Game.Board> = RxIndoorway.getPathsObservable()
        .observeOn(Schedulers.computation())
        .map { it.toBoard() }

private fun List<IndoorwayNode>.toBoard(): Game.Board {

    val center = first().coordinates.toPosition()

    val mapIdToNode = this.associateBy({ it.id }, { it })

    val mapGameNodeToNode = mapIdToNode.mapKeys { entry -> Game.Node(mapIdToNode[entry.key]!!.coordinates.toFullPosition(center)) }

    val mapGameNodeToNeighbours = mapGameNodeToNode.mapValues { entry ->
        entry.value.neighbours
                .map { mapIdToNode[it]!!.coordinates.toFullPosition(center) }
                .filterNotNull()
                .map { Game.Node(it) }
    }

    return Game.Board(center, mapGameNodeToNeighbours)
}

private fun Coordinates.toPosition() = Position(latitude, longitude)

private fun Coordinates.toFullPosition(center: Position) = toPosition().toFull(center)