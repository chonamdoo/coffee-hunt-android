package com.indoorway.coffeehunt.game.ar

import org.junit.Assert
import org.junit.Test
import com.indoorway.coffeehunt.game.ar.AR.RelativePositionOnScreen

class ToPositionOnScreen_Location_Test {

    private val horizontalViewAngle = 1.0952516964850147
    private val verticalViewAngle = 0.8579515

    @Test
    fun shouldShowObjectInTheMiddleOfTheScreen() {
        testFirstToPositionOnScreen()
                .assertLocationOnScreen(0f, 0f)
    }

    @Test
    fun shouldShowObjectOnTheTopOfTheScreen() {
        testFirstToPositionOnScreen(pitch = horizontalViewAngle / 2)
                .assertLocationOnScreen(1f, 0f)
    }

    @Test
    fun shouldShowObjectOnTheBottomOfTheScreen() {
        testFirstToPositionOnScreen(pitch = -horizontalViewAngle / 2)
                .assertLocationOnScreen(-1f, 0f)
    }

    @Test
    fun shouldShowObjectOnTheLeftOfTheScreen() {
        testFirstToPositionOnScreen(heading = -verticalViewAngle / 2)
                .assertLocationOnScreen(0f, -1f)
    }

    @Test
    fun shouldShowObjectOnTheRightOfTheScreen() {
        testFirstToPositionOnScreen(heading = verticalViewAngle / 2)
                .assertLocationOnScreen(0f, 1f)
    }

    @Test
    fun shouldNotShowObjectBehindUser() {
        testToPositionOnScreen(heading = Math.toRadians(180.0))
                .assertNoOnScreen()
    }

    @Test
    fun shouldShowObjectOnTheTopRightOfTheScreenForDifferentFieldOfView() {
        testFirstToPositionOnScreen(pitch = 0.6, heading = 0.4, horizontalViewAngle = 1.2, verticalViewAngle = 0.8)
                .assertLocationOnScreen(1f, 1f)
    }

    @Test
    fun shouldShowObjectOnTheRightOfTheScreenForDifferentSeedPosition() {
        testFirstToPositionOnScreen(heading = verticalViewAngle / 2 + 0.5, angleToItem = 0.5)
                .assertLocationOnScreen(0f, 1f)
    }

    @Test
    fun shouldShowObjectOnTheScreenIfAngleOverlaps() {
        testFirstToPositionOnScreen(heading = Math.toRadians(180.0), angleToItem = Math.toRadians(-180.0))
                .assertLocationOnScreen(0f, 0f)
    }

    private fun AR.Item.assertLocationOnScreen(expectedHeight: Float, expectedWidth: Float) = relativePositionOnScreen.assertLocationOnScreen(expectedHeight, expectedWidth)

    private fun RelativePositionOnScreen.assertLocationOnScreen(expectedHeight: Float, expectedWidth: Float) {
        Assert.assertEquals(expectedHeight, height, 0.0001f)
        Assert.assertEquals(expectedWidth, width, 0.0001f)
    }

    private fun <E> List<E>.assertNoOnScreen() = assert(isEmpty())
}
