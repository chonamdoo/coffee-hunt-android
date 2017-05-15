package com.indoorway.coffeehunt.common.intent

import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.VerificationModes
import android.support.test.espresso.intent.matcher.IntentMatchers

fun checkNoIntent(clazz: Class<*>) {
    Intents.intended(
            IntentMatchers.hasComponent(clazz.toString()),
            VerificationModes.noUnverifiedIntents())
}