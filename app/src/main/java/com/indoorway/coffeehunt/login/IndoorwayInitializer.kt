package com.indoorway.coffeehunt.login

import android.app.Application
import android.util.Log
import com.indoorway.android.location.sdk.IndoorwayLocationSdk
import com.indoorway.android.map.sdk.IndoorwayMapSdk

interface IndoorwayInitializer {
    fun init(token: String)
}

class AndroidIndoorwayInitializer(val application: Application) : IndoorwayInitializer {

    override fun init(token: String) {
        Log.w("INIT", "MAP SDK")
        IndoorwayMapSdk.init(application, token)
        Log.w("INIT", "LOC SDK")
        IndoorwayLocationSdk.init(application, token)
    }
}