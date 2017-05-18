package com.indoorway.coffeehunt.game.ar

import com.indoorway.coffeehunt.game.ar.AR.FieldOfView
import com.indoorway.coffeehunt.game.ar.AR.RelativePositionOnScreen
import com.indoorway.coffeehunt.game.core.*
import io.reactivex.Observable
import io.reactivex.functions.Function7
import io.reactivex.internal.functions.Functions

fun Observable<Iterable<Game.Item>>.toPositionOnScreen(
        pitch: PitchEvents,
        heading: HeadingEvents,
        roll: RollEvents,
        center: Observable<Position>,
        fieldOfView: Observable<AR.FieldOfView>,
        userPositions: Observable<Position>): Observable<List<AR.Item>> {

    return toPositionOnScreenInternal(pitch, heading, roll, center, fieldOfView, userPositions, FullPosition::getAngleTo, FullPosition::getDistanceTo)
}

typealias Combiner = Function7<AngleInRads, AngleInRads, AngleInRads, Position, FieldOfView, Position, Iterable<Game.Item>, List<AR.Item>>

internal fun Observable<Iterable<Game.Item>>.toPositionOnScreenInternal(
        pitch: PitchEvents,
        heading: HeadingEvents,
        roll: RollEvents,
        center: Observable<Position>,
        fieldOfView: Observable<FieldOfView>,
        userPositions: Observable<Position>,
        getAngleTo: FullPosition.(FullPosition) -> Double,
        getDistanceTo: FullPosition.(FullPosition) -> Double): Observable<List<AR.Item>> {

    val combiner = Combiner { pitch, heading, roll, center, fieldOfView, userPosition, items ->
        val sideHorizontalViewAngleInRads = fieldOfView.horizontalViewAngle / 2
        val horizontalReferenceDistanceToScreen = 1 / Math.tan(sideHorizontalViewAngleInRads)
        val sideVerticalViewAngleInRads = fieldOfView.verticalViewAngle / 2
        val verticalReferenceDistanceToScreen = 1 / Math.tan(sideVerticalViewAngleInRads)
        val fullPosition = userPosition.toFull(center)
        items.fold(mutableListOf<AR.Item>()) { acc, item ->
            val horizontalAngle = fullPosition.getAngleTo(item.position)
            val relativeAngle = heading - horizontalAngle
            val width = Math.tan(relativeAngle) * verticalReferenceDistanceToScreen
            val height = Math.tan(pitch) * horizontalReferenceDistanceToScreen
            val distance = fullPosition.getDistanceTo(item.position)
            if (relativeAngle.isHeadingTowards() && width.isWidthOnScreen() && distance.isInVisibilityRadius()) {
                acc.add(item.toARItem(RelativePositionOnScreen(
                        width.toFloat(),
                        height.toFloat(),
                        (1.0 / distance).toFloat(),
                        distance.toAlpha(),
                        roll
                )))
            }
            acc
        }
    }
    val bufferSize = 3
    return Observable.combineLatest(
            listOf(pitch, heading, roll, center, fieldOfView, userPositions, this),
            Functions.toFunction(combiner),
            bufferSize)
}

private fun Double.toAlpha() = (255 / FADE_OUT_RADIUS * VISIBILITY_RADIUS - 255 / FADE_OUT_RADIUS * this).boundToRange()

private fun Double.boundToRange() = Math.max(Math.min(255, toInt()), 0)

private fun AngleInRads.isHeadingTowards() = Math.cos(this) > 0

private fun Double.isWidthOnScreen() = this > -2.0 && this < 2.0

private fun Double.isInVisibilityRadius() = this < VISIBILITY_RADIUS

private const val VISIBILITY_RADIUS = 10

private const val FADE_OUT_RADIUS = 5
