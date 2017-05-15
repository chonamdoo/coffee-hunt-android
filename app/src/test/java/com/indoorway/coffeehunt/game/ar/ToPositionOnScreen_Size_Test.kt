package com.indoorway.coffeehunt.game.ar

import org.junit.Assert
import org.junit.Test
import com.indoorway.coffeehunt.game.ar.AR.RelativePositionOnScreen

class ToPositionOnScreen_Size_Test {

    @Test
    fun shouldShowObjectInNormalSizeWhenOneMeterFromUser() {
        testFirstToPositionOnScreen(distanceToItem = 1.0)
                .assertSizeOnScreen(1f)
    }

    @Test
    fun shouldShowObjectSmallerWhenFurtherThanOneMeterFromUser() {
        testFirstToPositionOnScreen(distanceToItem = 2.0)
                .assertSizeOnScreen(0.5f)
    }

    private fun AR.Item.assertSizeOnScreen(expectedSize: Float) = relativePositionOnScreen.assertSizeOnScreen(expectedSize)

    private fun RelativePositionOnScreen.assertSizeOnScreen(expectedSize: Float) {
        Assert.assertEquals(expectedSize, size, 0.0001f)
    }
}
