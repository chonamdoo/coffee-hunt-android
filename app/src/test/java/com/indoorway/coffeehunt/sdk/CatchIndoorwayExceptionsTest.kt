package com.indoorway.coffeehunt.sdk

import android.Manifest
import com.indoorway.android.common.sdk.exceptions.MissingPermissionException
import com.indoorway.android.common.tasks.exceptions.HttpException
import com.indoorway.android.location.sdk.exceptions.bluetooth.BLENotSupportedException
import com.indoorway.android.location.sdk.exceptions.bluetooth.BluetoothDisabledException
import com.indoorway.android.location.sdk.exceptions.location.LocationDisabledException
import com.indoorway.coffeehunt.game.sensors.IndoorwayException
import com.indoorway.coffeehunt.game.sensors.catchExceptions
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Observable
import org.junit.Test

class CatchIndoorwayExceptionsTest {

    private val showError: (String) -> Unit = mock()

    @Test
    fun shouldCatchHttpException() {
        Observable
                .error<Unit>(IndoorwayException(cause = HttpException(502, "")))
                .catchExceptions(showError)
                .subscribe()

        verify(showError).invoke("Network error")
    }

    @Test
    fun shouldNotThrowErrorOnHttpException() {
        Observable
                .error<Unit>(IndoorwayException(cause = HttpException(502, "")))
                .catchExceptions {}
                .test()
                .assertNoErrors()
    }

    @Test
    fun shouldCatchMissingCameraPermissionException() {
        Observable
                .error<Unit>(IndoorwayException(cause = MissingPermissionException(Manifest.permission.CAMERA)))
                .catchExceptions(showError)
                .subscribe()

        verify(showError).invoke("Missing camera permission")
    }

    @Test
    fun shouldCatchMissingLocationPermissionException() {
        Observable
                .error<Unit>(IndoorwayException(cause = MissingPermissionException(Manifest.permission.ACCESS_FINE_LOCATION)))
                .catchExceptions(showError)
                .subscribe()

        verify(showError).invoke("Missing location permission")
    }

    @Test
    fun shouldCatchLocationDisabledException() {
        Observable
                .error<Unit>(IndoorwayException(cause = LocationDisabledException()))
                .catchExceptions(showError)
                .subscribe()

        verify(showError).invoke("Location is disabled")
    }

    @Test
    fun shouldNotThrowErrorOnLocationDisabledException() {
        Observable
                .error<Unit>(IndoorwayException(cause = LocationDisabledException()))
                .catchExceptions {}
                .test()
                .assertNoErrors()
    }

    @Test
    fun shouldCatchBluetoothDisabledException() {
        Observable
                .error<Unit>(IndoorwayException(cause = BluetoothDisabledException()))
                .catchExceptions(showError)
                .subscribe()

        verify(showError).invoke("Bluetooth is disabled")
    }

    @Test
    fun shouldNotThrowErrorOnBluetoothDisabledException() {
        Observable
                .error<Unit>(IndoorwayException(cause = BluetoothDisabledException()))
                .catchExceptions {}
                .test()
                .assertNoErrors()
    }

    @Test
    fun shouldCatchBLENotSupportedException() {
        Observable
                .error<Unit>(IndoorwayException(cause = BLENotSupportedException()))
                .catchExceptions(showError)
                .subscribe()

        verify(showError).invoke("Bluetooth Low Energy is not supported")
    }

    @Test
    fun shouldNotThrowErrorOnBLENotSupportedException() {
        Observable
                .error<Unit>(IndoorwayException(cause = BLENotSupportedException()))
                .catchExceptions {}
                .test()
                .assertNoErrors()
    }

    @Test
    fun shouldThrowOtherErrors() {
        val exception = NullPointerException()
        Observable
                .error<Unit>(exception)
                .catchExceptions {}
                .test()
                .assertError(exception)
    }
}