package com.indoorway.coffeehunt.game.rules

import org.junit.Test

class KillingPlayerTest {

    @Test
    fun shouldKillPlayerOnMove() {
        listOf(newMonster()).let { monsters ->
            newState(monsters = monsters)
                    .updatePlayerPosition(0.0, 0.0)
                    .assertPlayerDead()
        }
    }

    @Test
    fun shouldNotKillPlayerOnMove() {
        listOf(newMonster()).let { monsters ->
            newState(monsters = monsters)
                    .updatePlayerPosition(1.0, 1.0)
                    .assertPlayerAlive()
        }
    }

    @Test
    fun shouldKillPlayerOnPulse() {
        listOf(newMonster(
                from = newPosition(0.0, 0.0, 1.0 to 1.0),
                to = newPosition(1.0, 1.0, 1.0 to 1.0),
                progress = 98)).let { monsters ->
            newState(monsters = monsters, board = newBoard(center = 1.0 to 1.0))
                    .updatePlayerPosition(1.0, 1.0)
                    .assertPlayerAlive()
                    .pulse()
                    .assertPlayerDead()
        }
    }

    @Test
    fun shouldNotKillPlayerOnPulse() {
        listOf(newMonster(
                from = newPosition(0.0, 0.0, 1.0 to 1.0),
                to = newPosition(1.0, 1.0, 1.0 to 1.0),
                progress = 97)).let { monsters ->
            newState(monsters = monsters, board = newBoard(center = 1.0 to 1.0))
                    .updatePlayerPosition(1.0, 1.0)
                    .assertPlayerAlive()
                    .pulse()
                    .assertPlayerAlive()
        }
    }
}
