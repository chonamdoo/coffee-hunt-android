package com.indoorway.coffeehunt.game.sensors

import android.Manifest
import android.widget.Toast
import com.indoorway.android.common.sdk.exceptions.MissingPermissionException
import com.indoorway.android.common.sdk.listeners.generic.Action1
import com.indoorway.android.common.sdk.listeners.position.OnHeadingChangedListener
import com.indoorway.android.common.sdk.listeners.position.OnPitchChangedListener
import com.indoorway.android.common.sdk.listeners.position.OnPositionChangedListener
import com.indoorway.android.common.sdk.listeners.position.OnRollChangedListener
import com.indoorway.android.common.sdk.model.IndoorwayNode
import com.indoorway.android.common.sdk.model.IndoorwayPosition
import com.indoorway.android.common.tasks.exceptions.HttpException
import com.indoorway.android.location.sdk.IndoorwayLocationSdk
import com.indoorway.android.location.sdk.exceptions.bluetooth.BLENotSupportedException
import com.indoorway.android.location.sdk.exceptions.bluetooth.BluetoothDisabledException
import com.indoorway.android.location.sdk.exceptions.location.LocationDisabledException
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
        data class PitchChange(val pitch: Float) : LocationSdkChange()
        data class RollChange(val roll: Float) : LocationSdkChange()
    }

    private val locationSdkChanges =
            Observable.create<LocationSdkChange> {
                val emitter = it.serialize()
                val sdk = IndoorwayLocationSdk.instance
                val connection = sdk.positioningServiceConnection
                val context = DI.provideApplicationContext()
                emitter.setCancellable { connection.stop(context) }
                connection.onPositionChangedListener = OnPositionChangedListener { emitter.onNext(LocationSdkChange.PositionChange(it)) }
                connection.onHeadingChangedListener = OnHeadingChangedListener { emitter.onNext(LocationSdkChange.HeadingChange(it)) }
                connection.onPitchChangedListener = OnPitchChangedListener { emitter.onNext(LocationSdkChange.PitchChange(it)) }
                connection.onRollChangedListener = OnRollChangedListener { emitter.onNext(LocationSdkChange.RollChange(it)) }
                try {
                    connection.start(context)
                } catch (e: Exception) {
                    emitter.onError(IndoorwayException("Positioning service failed", e))
                }
            }
                    .catchExceptions(this::showToast)
                    .logEvents("RX LOC SDK")
                    .share()

    val mapConfig: Single<Pair<String, String>> = locationSdkChanges
            .ofType(LocationSdkChange.PositionChange::class.java)
            .firstOrError()
            .map { it.position.buildingUuid to it.position.mapUuid }

    private val buildingApiMapObjects =
            mapConfig.flatMap { (buildingUUID, mapUUID) ->
                Single.create<IndoorwayMap> { emitter ->
                    val sdk = IndoorwayMapSdk.instance
                    sdk.buildingsApi.getMapObjects(buildingUUID, mapUUID).apply {
                        setOnCompletedListener(Action1 {
                            emitter.onSuccess(it)
                        })
                        setOnFailedListener(Action1 {
                            emitter.onError(IndoorwayException("Getting map objects failed", it as Throwable))
                        })
                        execute()
                    }

                }
            }

    private val buildingApiPaths = buildingApiMapObjects
            .map { it.paths }
            .toObservable()
            .catchExceptions(this::showToast)
            .logEvents("RX MAP SDK")
            .share()

    fun getUserPositionsObservable(): Observable<IndoorwayPosition> = locationSdkChanges
            .ofType(LocationSdkChange.PositionChange::class.java)
            .map { it.position }

    fun getHeadingObservable(): Observable<Double> = locationSdkChanges
            .ofType(LocationSdkChange.HeadingChange::class.java)
            .map { it.heading.toDouble() }

    fun getPitchObservable(): Observable<Double> = locationSdkChanges
            .ofType(LocationSdkChange.PitchChange::class.java)
            .map { Math.toRadians(it.pitch.toDouble() + 90.0) }

    fun getRollObservable(): Observable<Double> = locationSdkChanges
            .ofType(LocationSdkChange.RollChange::class.java)
            .map { Math.toRadians(it.roll.toDouble()) }

    fun getPathsObservable(): Observable<List<IndoorwayNode>> = buildingApiPaths

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