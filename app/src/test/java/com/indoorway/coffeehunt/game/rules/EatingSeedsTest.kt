package com.indoorway.coffeehunt.game.rules

import org.junit.Test

class EatingSeedsTest {

    @Test
    fun shouldEatASeedWhenCloseEnough() {
        setOf(newSeed(0.0, 0.0)).let { seeds ->
            newState(seeds = seeds)
                    .updatePlayerPosition(0.0, 0.0)
                    .assertSeeds(emptySet())
        }
    }

    @Test
    fun shouldNotEatASeedWhenNotCloseEnough() {
        setOf(newSeed(0.0, 0.0)).let { seeds ->
            newState(seeds = seeds)
                    .updatePlayerPosition(1.0, 1.0)
                    .assertSeeds(seeds)
        }
    }

}