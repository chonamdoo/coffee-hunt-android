package com.indoorway.coffeehunt.common

import android.app.Activity
import android.app.Instrumentation
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers
import com.indoorway.coffeehunt.game.GameActivity
import com.indoorway.coffeehunt.permission.PermissionsActivity

fun stubGameActivityIntentResponseToCanceled() {
    stubActivity(GameActivity::class.java)
}

fun stubPermissionActivityIntentResponseToCanceled() {
    stubActivity(PermissionsActivity::class.java)
}

private fun stubActivity(activity: Class<out Activity>) {
    Intents.intending(IntentMatchers.hasComponent(activity.name))
            .respondWith(Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null))
}
