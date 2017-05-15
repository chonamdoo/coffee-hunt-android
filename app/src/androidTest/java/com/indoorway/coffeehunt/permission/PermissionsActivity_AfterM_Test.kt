package com.indoorway.coffeehunt.permission

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.support.test.filters.SdkSuppress
import android.support.test.rule.ActivityTestRule
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.indoorway.coffeehunt.R
import com.indoorway.coffeehunt.common.intent.checkNoIntent
import com.indoorway.coffeehunt.common.stubGameActivityIntentResponseToCanceled
import com.indoorway.coffeehunt.game.GameActivity

@SuppressLint("NewApi")
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.M)
class PermissionsActivity_AfterM_Test {

    @JvmField @Rule
    val intents = InitIntentsRule()

    @JvmField @Rule
    val rule = ActivityTestRule<PermissionsActivity>(PermissionsActivity::class.java, false, false)

    @Before
    fun setUp() {
        stubGameActivityIntentResponseToCanceled()
    }

    @Test
    fun shouldShowCameraPermissionDescription() {
        stubNoPermissions()
        lunchActivity()
        onText(R.string.camera_permission_desc).isDisplayed()
    }

    @Test
    fun shouldShowLocationPermissionDescription() {
        stubNoPermissions()
        lunchActivity()
        onText(R.string.location_permission_desc).isDisplayed()
    }

    @Test
    fun shouldShowScreenTitle() {
        stubNoPermissions()
        lunchActivity()
        onText(R.string.required_permissions).isDisplayed()
    }

    @Test
    fun shouldNotFinishImmediatelyWithoutAnyPermission() {
        stubNoPermissions()
        lunchActivity()
        assertFalse(rule.activity.isFinishing)
    }

    @Test
    fun shouldNotFinishImmediatelyWithoutCameraPermission() {
        stubLocationPermission()
        lunchActivity()
        assertFalse(rule.activity.isFinishing)
    }

    @Test
    fun shouldNotFinishImmediatelyWithoutLocationPermission() {
        stubCameraPermission()
        lunchActivity()
        assertFalse(rule.activity.isFinishing)
    }

    @Test
    fun shouldFinishImmediatelyWhenAllPermissionsGranted() {
        stubAllPermissions()
        lunchActivity()
        assertTrue(rule.activity.isFinishing)
    }

    @Test
    fun shouldOpenGameScreenWhenAllPermissionsGranted() {
        stubNoPermissions().andAllow()
        lunchActivity()
        onId(R.id.grantPermissionsButton).click()
        checkIntent(GameActivity::class.java)
    }

    @Test
    fun shouldNotOpenGameScreenWhenCameraPermissionDenied() {
        stubNoPermissions().andReject()
        lunchActivity()
        onId(R.id.grantPermissionsButton).click()
        checkNoIntent(GameActivity::class.java)
    }

    private fun stubNoPermissions() =
            stubPermissions {
                whenever(isPermissionGranted(any())).thenReturn(false)
            }

    private fun stubCameraPermission() =
            stubPermissions {
                whenever(isPermissionGranted(Manifest.permission.CAMERA)).thenReturn(true)
                whenever(isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)).thenReturn(false)
            }

    private fun stubLocationPermission() =
            stubPermissions {
                whenever(isPermissionGranted(Manifest.permission.CAMERA)).thenReturn(false)
                whenever(isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)).thenReturn(true)
            }

    private fun stubAllPermissions() =
            stubPermissions {
                whenever(isPermissionGranted(any())).thenReturn(true)
            }

    private fun PermissionsInteractor.andAllow() =
            whenever(showRequestPermissionsDialog(any(), any())).then {
                callOnPermissionResult(it.getArgument(0), intArrayOf(PackageManager.PERMISSION_GRANTED))
            }

    private fun PermissionsInteractor.andReject() =
            whenever(showRequestPermissionsDialog(any(), any())).then {
                callOnPermissionResult(it.getArgument(0), intArrayOf(PackageManager.PERMISSION_DENIED))
            }


    private fun callOnPermissionResult(requestCode: Int, grantResults: IntArray) =
            rule.activity.onRequestPermissionsResult(requestCode, emptyArray(), grantResults)

    private fun stubPermissions(additionalStubbing: PermissionsInteractor.() -> Unit) =
            mock<PermissionsInteractor>().apply {
                whenever(shouldCheckPermissions()).thenReturn(true)
                additionalStubbing()
                DI.providePermissionsIteractor = { this }
            }

    private fun lunchActivity() = rule.launchActivity(null)
}
