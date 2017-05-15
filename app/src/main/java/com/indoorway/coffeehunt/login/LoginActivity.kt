package com.indoorway.coffeehunt.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.indoorway.android.qrcode.sdk.IndoorwayQrCodeSdk
import kotlinx.android.synthetic.main.login_activity.*
import com.indoorway.coffeehunt.R
import com.indoorway.coffeehunt.common.DI.provideLoginRepository
import com.indoorway.coffeehunt.common.enterImmersiveFullScreenMode
import com.indoorway.coffeehunt.permission.PermissionsActivity

class LoginActivity : AppCompatActivity(), Login.View {

    private val controller = LoginController(this, provideLoginRepository(), DI.provideIndoorwayInitializer())
    private val RC_QR_SCAN = 62483

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        controller.onCreate()
        scanQrCodeButton.setOnClickListener {
            DI.provideOpenQRCodeScreenAction(this, RC_QR_SCAN)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) = enterImmersiveFullScreenMode(hasFocus)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_QR_SCAN && resultCode == Activity.RESULT_OK && data != null) {
            IndoorwayQrCodeSdk.getInstance().getQrCodeFromIntent(data)?.let {
                controller.onToken(it)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun openPermissionsScreen() {
        startActivity(Intent(this, PermissionsActivity::class.java))
        finish()
    }
}