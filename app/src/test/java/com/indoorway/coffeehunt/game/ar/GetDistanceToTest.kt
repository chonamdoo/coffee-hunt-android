package com.indoorway.coffeehunt.game.ar

import org.junit.Assert
import org.junit.Test
import com.indoorway.coffeehunt.game.core.getDistanceTo
import com.indoorway.coffeehunt.game.rules.newPosition

class GetDistanceToTest {

    @Test
    fun shouldTranslateLocationToNonZeroDistance() {
        newFullPosition(52.0, 21.0).getDistanceTo(newFullPosition(52.0, 20.9))
                .assertDistance { it > 0 }
    }

    @Test
    fun shouldTranslateLocationToZeroDistance() {
        newFullPosition(52.0, 21.0).getDistanceTo(newFullPosition(52.0, 21.0))
                .assertDistance { it == 0.0 }
    }

    private fun newFullPosition(latitude: Double, longitude: Double) = newPosition(latitude, longitude, 52.0 to 21.0)

    private fun Distance.assertDistance(distancePredicate: (Distance) -> Boolean) {
        Assert.assertTrue("Distance $this doesn't match predicate", distancePredicate(this))
    }
}
