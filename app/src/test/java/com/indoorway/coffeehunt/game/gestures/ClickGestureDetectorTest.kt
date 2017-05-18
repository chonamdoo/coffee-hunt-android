package com.indoorway.coffeehunt.game.gestures

import android.view.MotionEvent
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test

class ClickGestureDetectorTest {

    val downEvent = mock<MotionEvent> {
        on { action }.thenReturn(MotionEvent.ACTION_DOWN)
    }

    val upEvent = mock<MotionEvent> {
        on { action }.thenReturn(MotionEvent.ACTION_UP)
    }

    @Test
    fun shouldDetectClick() {
        val instance = ClickGestureDetector()
        val subscription = instance.clicks.test()
        subscription.assertNoValues()
        instance.onTouch(mock {  }, downEvent)
        subscription.assertNoValues()
        instance.onTouch(mock {  }, upEvent)
        subscription.assertValueCount(1)
    }

    @Test
    fun shouldNotDetectClick() {
        val instance = ClickGestureDetector()
        val subscription = instance.clicks.test()
        subscription.assertNoValues()
        instance.onTouch(mock {  }, downEvent)
        subscription.assertNoValues()
        Thread.sleep(instance.MAX_CLICK_DURATION + 1)
        instance.onTouch(mock {  }, upEvent)
        subscription.assertNoValues()
    }

}