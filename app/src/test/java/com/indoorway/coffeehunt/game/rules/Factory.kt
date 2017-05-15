package com.indoorway.coffeehunt.game.rules

import com.indoorway.coffeehunt.game.core.Game
import com.indoorway.coffeehunt.game.core.Progress
import com.indoorway.coffeehunt.game.core.FullPosition
import com.indoorway.coffeehunt.game.core.Position
import com.indoorway.coffeehunt.game.core.toFull

fun newMonster(
        from: Pair<Double, Double> = 0.0 to 0.0,
        to: Pair<Double, Double> = 1.0 to 1.0,
        progress: Progress = 0) = newMonster(newPosition(from.first, from.second), newPosition(to.first, to.second), progress)

fun newMonster(
        from: FullPosition = newPosition(0.0, 0.0),
        to: FullPosition,
        progress: Progress) = Game.Monster(newNode(from), newNode(to), progress, "monsterKey")

fun newSeed(latitude: Double = 0.0, longitude: Double = 0.0) = Game.Seed.Regular(newPosition(latitude, longitude), "regularSeedKey")

fun newNode(latitude: Double = 0.0, longitude: Double = 0.0) = newNode(newPosition(latitude, longitude))

fun newNode(position: FullPosition) = Game.Node(position)

fun newBoard(from: Game.Node, to: Game.Node) = newBoard(nodes = mapOf(from to listOf(to)))

fun newBoard(
        center: Pair<Double, Double> = 0.0 to 0.0,
        nodes: Map<Game.Node, List<Game.Node>> = emptyMap()) = Game.Board(Position(center.first, center.second), nodes)

fun newState(
        monsters: List<Game.Monster> = emptyList(),
        seeds: Set<Game.Seed> = emptySet(),
        board: Game.Board = newBoard()) = Game.State(Game.Player.None, monsters, seeds, board)

fun newPosition(
        latitude: Double,
        longitude: Double,
        center: Pair<Double, Double> = 0.0 to 0.0) = Position(latitude, longitude).toFull(Position(center.first, center.second))
