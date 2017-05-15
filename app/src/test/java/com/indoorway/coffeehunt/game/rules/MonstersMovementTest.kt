package com.indoorway.coffeehunt.game.rules

import org.junit.Test

class MonstersMovementTest {

    @Test
    fun shouldMoveMonsters() {
        listOf(newMonster()).let { monsters ->
            newState(monsters = monsters)
                    .pulse()
                    .assertMonsters(listOf(newMonster(progress = 2)))
        }
    }

    @Test
    fun shouldMonsterTurnAroundWhenReachingTheEndAndNoOtherNeighboursAvailable() {
        listOf(newMonster(progress = 99)).let { monsters ->
            newState(monsters = monsters)
                    .pulse()
                    .assertMonsters(listOf(newMonster(
                            from = 1.0 to 1.0,
                            to = 0.0 to 0.0,
                            progress = 1)))
        }
    }

    @Test
    fun shouldMonsterTurnToAvailableNeighbour() {
        val board = newBoard(newNode(1.0, 1.0), newNode(2.0, 2.0))
        val monsters = listOf(newMonster(progress = 99))
        newState(monsters = monsters, board = board)
                .pulse()
                .assertMonsters(listOf(newMonster(
                        from = 1.0 to 1.0,
                        to = 2.0 to 2.0,
                        progress = 1)))
    }
}