package com.indoorway.coffeehunt

import android.support.multidex.MultiDexApplication
import com.indoorway.coffeehunt.game.DI

class GameApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        DI.provideApplicationContext = { this }
    }
}