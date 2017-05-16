package com.indoorway.coffeehunt.game.sensors

import android.Manifest
import android.widget.Toast
import com.indoorway.android.common.exceptions.http.HttpException
import com.indoorway.android.common.sdk.exceptions.MissingPermissionException
import com.indoorway.android.common.sdk.model.IndoorwayPosition
import com.indoorway.android.common.sdk.task.IndoorwayTask
import com.indoorway.android.location.sdk.IndoorwayLocationSdk
import com.indoorway.android.location.sdk.exceptions.bluetooth.BLENotSupportedException
import com.indoorway.android.location.sdk.exceptions.bluetooth.BluetoothDisabledException
import com.indoorway.android.location.sdk.exceptions.location.LocationDisabledException
import com.indoorway.android.location.sdk.service.PositioningServiceConnection
import com.indoorway.android.map.sdk.IndoorwayMapSdk
import com.indoorway.android.map.sdk.model.IndoorwayMap
import com.indoorway.coffeehunt.common.logEvents
import com.indoorway.coffeehunt.game.DI
import io.reactivex.Observable
import io.reactivex.Single

object RxIndoorway {

    private sealed class LocationSdkChange {
        data class PositionChange(val position: IndoorwayPosition) : LocationSdkChange()
        data class HeadingChange(val heading: Float) : LocationSdkChange()
    }

    private val locationSdkChanges =
            Observable.create<LocationSdkChange> {
                val emitter = it.serialize()
                val sdk = IndoorwayLocationSdk.getInstance()
                val connection = sdk.positioningServiceConnection
                val context = DI.provideApplicationContext()
                emitter.setCancellable { connection.stop(context) }
                connection.setOnPositionChangedListener<PositioningServiceConnection> { emitter.onNext(LocationSdkChange.PositionChange(it)) }
                connection.setOnHeadingChangedListener<PositioningServiceConnection> { emitter.onNext(LocationSdkChange.HeadingChange(it)) }
                try {
                    connection.start(context)
                } catch (e: Exception) {
                    emitter.onError(IndoorwayException("Positioning service failed", e))
                }
            }
                    .catchExceptions(this::showToast)
                    .logEvents("RX LOC SDK")
                    .share()

    val mapConfig = Single.just("" to "")

    private val buildingApiMapObjects =
            mapConfig.flatMap { (buildingUUID, mapUUID) ->
                Single.create<IndoorwayMap> { emitter ->
                    val sdk = IndoorwayMapSdk.getInstance()
                    sdk.buildingsApi.getMapObjects(buildingUUID, mapUUID)
                            .setOnCompletedListener<IndoorwayTask<IndoorwayMap>> {
                                emitter.onSuccess(it)
                            }
                            .setOnFailedListener<IndoorwayTask<IndoorwayMap>> {
                                emitter.onError(IndoorwayException("Getting map objects failed", it as Throwable))
                            }
                            .execute()
                }
            }

    private val buildingApiPaths = buildingApiMapObjects
            .map { it.paths }
            .toObservable()
            .catchExceptions(this::showToast)
            .logEvents("RX MAP SDK")
            .share()

    fun getUserPositionsObservable() = locationSdkChanges
            .ofType(LocationSdkChange.PositionChange::class.java)
            .map { it.position }

    fun getHeadingObservable() = locationSdkChanges
            .ofType(LocationSdkChange.HeadingChange::class.java)
            .map { it.heading }

    fun getPathsObservable() = buildingApiPaths

    private fun showToast(message: String) {
        val context = DI.provideApplicationContext()
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

class IndoorwayException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)

fun <T> Observable<T>.catchExceptions(showError: (String) -> Unit): Observable<T> = onErrorResumeNext { throwable: Throwable ->
    if (throwable is IndoorwayException) {
        catchIndoorwayExceptions<T>(throwable, showError)
    } else {
        Observable.error(throwable)
    }
}

fun <T> catchIndoorwayExceptions(exception: IndoorwayException, showError: (String) -> Unit): Observable<T> = when (exception.cause) {
    is HttpException -> {
        showError("Network error")
        Observable.empty()
    }
    is MissingPermissionException -> {
        val permission = exception.cause.permission
        showError(when (permission) {
            Manifest.permission.ACCESS_FINE_LOCATION -> "Missing location permission"
            Manifest.permission.CAMERA -> "Missing camera permission"
            else -> "Missing permission: $permission"
        })
        Observable.empty()
    }
    is LocationDisabledException -> {
        showError("Location is disabled")
        Observable.empty()
    }
    is BluetoothDisabledException -> {
        showError("Bluetooth is disabled")
        Observable.empty()
    }
    is BLENotSupportedException -> {
        showError("Bluetooth Low Energy is not supported")
        Observable.empty()
    }
    else -> Observable.error(exception)
}