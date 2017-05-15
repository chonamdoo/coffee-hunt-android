package com.indoorway.coffeehunt.login

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers.hasAction
import android.support.test.rule.ActivityTestRule
import com.elpassion.android.commons.espresso.InitIntentsRule
import com.elpassion.android.commons.espresso.checkIntent
import com.elpassion.android.commons.espresso.click
import com.elpassion.android.commons.espresso.onId
import com.indoorway.android.qrcode.sdk.IndoorwayQrCodeSdk
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.indoorway.coffeehunt.R
import com.indoorway.coffeehunt.common.DI.provideLoginRepository
import com.indoorway.coffeehunt.common.intent.checkNoIntent
import com.indoorway.coffeehunt.common.stubPermissionActivityIntentResponseToCanceled
import com.indoorway.coffeehunt.login.DI.provideOpenQRCodeScreenAction
import com.indoorway.coffeehunt.permission.PermissionsActivity

class LoginActivityTest {

    @JvmField @Rule
    val intents = InitIntentsRule()

    @JvmField @Rule
    val rule = ActivityTestRule<LoginActivity>(LoginActivity::class.java, false, false)

    @Before
    fun setUp() {
        stubPermissionActivityIntentResponseToCanceled()
    }

    @Test
    fun shouldFinishLoginActivityWhenTokenIsAvailable() {
        stubRepositoryToReturnToken("token")
        launchLoginActivity()
        assertTrue(rule.activity.isFinishing)
    }

    @Test
    fun shouldOpenPermissionsScreenWhenTokenIsAvailable() {
        stubRepositoryToReturnToken("token")
        launchLoginActivity()
        checkIntent(PermissionsActivity::class.java)
    }

    @Test
    fun shouldNotOpenPermissionsScreenWhenTokenIsNotAvailable() {
        stubRepositoryToReturnToken(null)
        launchLoginActivity()
        checkNoIntent(PermissionsActivity::class.java)
    }

    @Test
    fun shouldOpenPermissionsScreenWhenSignedInWithQrCode() {
        stubRepositoryToReturnToken(null)
        stubQRScannerToReturnToken("token")
        launchLoginActivity()
        requestQRCode()
        checkIntent(PermissionsActivity::class.java)
    }

    @Test
    fun shouldNotOpenPermissionsScreenWhenQRScannerCanceled() {
        stubRepositoryToReturnToken(null)
        stubQRScannerToCancel()
        launchLoginActivity()
        requestQRCode()
        checkNoIntent(PermissionsActivity::class.java)
    }

    private fun stubRepositoryToReturnToken(token: String?) {
        provideLoginRepository = { mock<Login.Repository>().apply { whenever(this.token).thenReturn(token) } }
    }

    private fun stubQRScannerToReturnToken(token: String) {
        val intentWithToken = Intent().apply { IndoorwayQrCodeSdk.getInstance().addQrCodeToIntent(this, token) }
        provideOpenQRCodeScreenAction = returnIntentFromQRScanner(Activity.RESULT_OK, intentWithToken)
    }

    private fun stubQRScannerToCancel() {
        provideOpenQRCodeScreenAction = returnIntentFromQRScanner(Activity.RESULT_CANCELED, null)
    }

    private fun returnIntentFromQRScanner(resultCode: Int, intent: Intent?): (Activity, Int) -> Unit {
        val actionName = "QRAutoFinishingIntent"
        Intents
                .intending(hasAction(actionName))
                .respondWith(ActivityResult(resultCode, intent))
        return { activity: Activity, requestCode: Int ->
            activity.startActivityForResult(Intent(actionName), requestCode)
        }
    }

    private fun launchLoginActivity() {
        rule.launchActivity(null)
    }

    private fun requestQRCode() = onId(R.id.scanQrCodeButton).click()
}
