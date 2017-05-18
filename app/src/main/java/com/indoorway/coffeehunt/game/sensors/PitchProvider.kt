package com.indoorway.coffeehunt.game.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.indoorway.coffeehunt.common.slowdown
import com.indoorway.coffeehunt.game.ar.PitchEvents
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

fun getPitchEvents(applicationContext: Context): PitchEvents =
        getSensorEvents(applicationContext, Sensor.TYPE_GRAVITY)
                .slowdown()
                .map(SensorEvent::toGravityRepresentation)
                .map(Gravity::calculateDeviationFromHorizontalLevel)

private fun SensorEvent.toGravityRepresentation() =
        Gravity(values[0].toDouble(), values[1].toDouble(), values[2].toDouble())

private data class Gravity(private val axis_X: Double, private val axis_Y: Double, private val axis_Z: Double) {

    fun calculateDeviationFromHorizontalLevel(): Double {
        val vectorLengthWithoutAxis_Z = Math.sqrt(axis_X * axis_X + axis_Y * axis_Y)
        val vectorLength = Math.sqrt(axis_X * axis_X + axis_Y * axis_Y + axis_Z * axis_Z)
        val normalizedVectorWithoutAxis_Z = vectorLengthWithoutAxis_Z / vectorLength
        val absoluteDeviationFromVerticalLevel = Math.asin(normalizedVectorWithoutAxis_Z)
        val absoluteDeviationFromHorizontalLevel = Math.PI / 2 - absoluteDeviationFromVerticalLevel
        val Z_AxisTurn = Math.signum(axis_Z)
        val deviationFromHorizontalLevelWithSpecifiedTurn = absoluteDeviationFromHorizontalLevel * Z_AxisTurn
        return deviationFromHorizontalLevelWithSpecifiedTurn
    }

}

private fun getSensorEvents(context: Context, sensorType: Int): Observable<SensorEvent> {
    return Observable.create<SensorEvent> { emitter ->
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(sensorType)
        val sensorEventListener = createSensorEventListener(emitter)
        emitter.setCancellable { sensorManager.unregisterListener(sensorEventListener, sensor) }
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_GAME)
    }
}

private fun createSensorEventListener(emitter: ObservableEmitter<SensorEvent>): SensorEventListener {
    return object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

        override fun onSensorChanged(event: SensorEvent) {
            emitter.onNext(event)
        }
    }
}
