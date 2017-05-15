package com.indoorway.coffeehunt.permission

import android.support.test.rule.ActivityTestRule
import com.elpassion.android.commons.espresso.InitIntentsRule
import com.elpassion.android.commons.espresso.checkIntent
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.indoorway.coffeehunt.common.stubGameActivityIntentResponseToCanceled
import com.indoorway.coffeehunt.game.GameActivity
import com.indoorway.coffeehunt.permission.DI.providePermissionsIteractor

class PermissionsActivity_BeforeM_Test {

    @JvmField @Rule
    val intents = InitIntentsRule()

    @JvmField @Rule
    val rule = ActivityTestRule<PermissionsActivity>(PermissionsActivity::class.java, false, false)

    @Before
    fun setUp() {
        stubGameActivityIntentResponseToCanceled()
    }

    @Test
    fun shouldFinishImmediatelyWhenBeforeM() {
        stubLikeBeforeM()
        rule.launchActivity(null)
        assertTrue(rule.activity.isFinishing)
    }

    @Test
    fun shouldStartGameActivityImmediatelyWhenBeforeM() {
        stubLikeBeforeM()
        rule.launchActivity(null)
        checkIntent(GameActivity::class.java)
    }

    private fun stubLikeBeforeM() {
        providePermissionsIteractor = {
            mock<PermissionsInteractor>().apply {
                whenever(shouldCheckPermissions()).thenReturn(false)
            }
        }
    }
}
