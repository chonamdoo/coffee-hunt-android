package com.indoorway.coffeehunt.login

import android.app.Activity
import com.indoorway.android.qrcode.sdk.IndoorwayQrCodeSdk
import com.indoorway.coffeehunt.game.DI

object DI {

    var provideOpenQRCodeScreenAction: (Activity, Int) -> Unit = IndoorwayQrCodeSdk.instance::startQrCodeActivity

    var provideIndoorwayInitializer: () -> IndoorwayInitializer = { AndroidIndoorwayInitializer(DI.provideApplicationContext()) }
}