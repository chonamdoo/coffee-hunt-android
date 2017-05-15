package com.indoorway.coffeehunt.game.rules

import org.junit.Test

class PlayerRulesTest {

    @Test
    fun shouldDisplayPlayer() {
        newState()
                .updatePlayerPosition(666.0, 777.0)
                .assertPlayerPosition(666.0, 777.0)
    }

    @Test
    fun shouldMovePlayerAccordingToGivenPositions() {
        newState()
                .updatePlayerPosition(666.0, 777.0)
                .updatePlayerPosition(888.0, 999.0)
                .assertPlayerPosition(888.0, 999.0)
    }

    @Test
    fun shouldMovePlayerAndLeaveMonstersAsTheyWere() {
        listOf(newMonster()).let { monsters ->
            newState(monsters = monsters)
                    .updatePlayerPosition(666.0, 777.0)
                    .assertMonsters(monsters)
        }
    }

    @Test
    fun shouldMovePlayerAndLeaveSeedsAsTheyWere() {
        setOf(newSeed()).let { seeds ->
            newState(seeds = seeds)
                    .updatePlayerPosition(666.0, 777.0)
                    .assertSeeds(seeds)
        }
    }

    @Test
    fun shouldMovePlayerAndLeaveBoardAsItWas() {
        newBoard(newNode(), newNode(1.1, 1.1)).let { board ->
            newState(board = board)
                    .updatePlayerPosition(666.0, 777.0)
                    .assertBoard(board)
        }
    }
}
