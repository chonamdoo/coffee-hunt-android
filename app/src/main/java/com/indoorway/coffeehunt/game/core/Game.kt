package com.indoorway.coffeehunt.game.core

import com.indoorway.coffeehunt.common.concat
import java.util.*

object Game {

    val RANDOM = Random()

    enum class Phase { IDLE, STARTED, FINISHED }

    data class State(val player: Player, val monsters: List<Monster>, val seeds: Set<Seed>, val board: Board)

    val EMPTY_STATE = State(Player.None, emptyList(), emptySet(), Board(Position(0.0, 0.0), emptyMap()))

    interface Item {
        val position: FullPosition
    }

    interface MapItem {
        val key: String
    }

    sealed class Player {

        open val score: Score get() = Score()

        object None : Player()

        sealed class Existent : Player(), Item {
            data class Alive(override val position: FullPosition, override val score: Score) : Existent()
            data class Dead(override val position: FullPosition, override val score: Score) : Existent()
        }

        fun tryToResurrect() = when (this) {
            is Existent -> Existent.Alive(position, Score())
            else -> this
        }
    }

    data class Score(val seeds: List<Seed> = emptyList()) : Comparable<Score> {

        val points = seeds.sumBy(Seed::points)

        override fun compareTo(other: Score) = points.compareTo(other.points)

        operator fun plus(seeds: Set<Seed>) = this.seeds + seeds
    }

    data class Node(override val position: FullPosition) : Item, MapItem {
        override val key: String = "Node ${position.hashCode()}"
    }

    sealed class Seed(override val position: FullPosition, override val key: String) : Item, MapItem {

        abstract val points: Int

        data class Regular(override val position: FullPosition, override val key: String) : Seed(position, key) {
            override val points: Int = REGULAR_SEED_POINTS
        }

        data class Special(override val position: FullPosition, override val key: String) : Seed(position, key) {
            override val points: Int = SPECIAL_SEED_POINTS
        }
    }

    data class Monster(val from: Node, val to: Node, val progress: Progress, override val key: String) : Item, MapItem {
        override val position get() = getPositionBetween(from.position, to.position, progress)

        fun move(board: Board) = moveForward().let { if (it.progress < 100) it else it.turn(board) }

        private fun moveForward() = copy(progress = progress + 2)

        private fun turn(board: Board) = copy(
                from = to,
                to = board.getRandomNeighbourOrDefault(node = to, except = from, default = from),
                progress = progress - 100)

    }

    data class Board(val center: Position, private val nodes: Map<Node, Neighbours>) {

        fun getNewGameState() = State(Player.None, generateMonsters(), generateSeeds(), this)

        fun neighbours(node: Node) = nodes[node] ?: emptyList()

        fun getRandomNeighbourOrDefault(node: Node, default: Node, except: Node? = null) = neighbours(node).filter { it != except }.getRandomOrDefault(default)

        private fun generateSeeds(): Set<Seed> = nodes.keys.map(this::newSeed).toSet()

        private fun newSeed(node: Node) = if (RANDOM.nextDouble() > 0.075) {
            Seed.Regular(node.position, "r" + node.key)
        } else {
            Seed.Special(node.position, "s" + node.key)
        }

        private fun generateMonsters(): List<Monster> = nodes.keys.toList().let { nodes ->
            (1..nodes.size / 16).map {
                nodes[RANDOM.nextInt(nodes.size)].let { node ->
                    Monster(node, getRandomNeighbourOrDefault(node, node), 0, node.key)
                }
            }
        }
    }

    private fun <T> List<T>.getRandomOrDefault(default: T) = if (size == 0) default else get(RANDOM.nextInt(size))

    fun createEmptyState() =
            Game.State(Game.Player.None, emptyList(), emptySet(), Game.Board(Position(0.0, 0.0), emptyMap()))
}

typealias Progress = Int // 0..100

typealias Neighbours = List<Game.Node>

val Game.State.items get() = concat(seeds, monsters)
