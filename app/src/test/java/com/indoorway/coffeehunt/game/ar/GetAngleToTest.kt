package com.indoorway.coffeehunt.game.ar

import org.junit.Assert
import org.junit.Test
import com.indoorway.coffeehunt.game.core.getAngleTo
import com.indoorway.coffeehunt.game.rules.newPosition

class GetAngleToTest {

    @Test
    fun shouldTranslateLocationOnTheNorthFromUser() {
        newPosition(52.0, 21.0).getAngleTo(newPosition(52.1, 21.0))
                .assertAngle(0.0)
    }

    @Test
    fun shouldTranslateLocationOnTheSouthFromUser() {
        newPosition(52.0, 21.0).getAngleTo(newPosition(51.9, 21.0))
                .assertAngle(Math.toRadians(180.0))
    }

    @Test
    fun shouldTranslateLocationOnTheEastFromUser() {
        newPosition(52.0, 21.0).getAngleTo(newPosition(52.0, 21.1))
                .assertAngle(Math.toRadians(90.0))
    }

    @Test
    fun shouldTranslateLocationOnTheWestFromUser() {
        newPosition(52.0, 21.0).getAngleTo(newPosition(52.0, 20.9))
                .assertAngle(Math.toRadians(-90.0))
    }

    @Test
    fun shouldConsiderEarthShapeWhileCalculatingAngle() {
        val unexpectedAngle = newPosition(0.0, 0.0).getAngleTo(newPosition(0.1, 0.1))
        val actualAngle = newPosition(50.0, 50.0).getAngleTo(newPosition(50.1, 50.1))
        actualAngle.assertAngleNotEqual(unexpectedAngle)
    }

    private fun AngleInRads.assertAngle(expectedAngle: AngleInRads) {
        Assert.assertEquals(expectedAngle, this, 0.0000001)
    }

    private fun AngleInRads.assertAngleNotEqual(unexpectedAngle: AngleInRads) {
        Assert.assertNotEquals(unexpectedAngle, this, 0.001)
    }
}
