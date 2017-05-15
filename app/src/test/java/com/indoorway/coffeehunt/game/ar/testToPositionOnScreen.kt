package com.indoorway.coffeehunt.game.ar

import io.reactivex.Observable
import com.indoorway.coffeehunt.game.core.Game
import com.indoorway.coffeehunt.game.core.Position
import com.indoorway.coffeehunt.game.rules.newPosition

fun testFirstToPositionOnScreen(
        pitch: Double = 0.0,
        heading: Double = 0.0,
        horizontalViewAngle: AngleInRads = 1.0952516964850147,
        verticalViewAngle: AngleInRads = 0.8579515,
        angleToItem: AngleInRads = 0.0,
        distanceToItem: Distance = 1.0
): AR.Item
        = testToPositionOnScreen(pitch, heading, horizontalViewAngle, verticalViewAngle, angleToItem, distanceToItem).first()


fun testToPositionOnScreen(
        pitch: Double = 0.0,
        heading: Double = 0.0,
        horizontalViewAngle: AngleInRads = 1.0952516964850147,
        verticalViewAngle: AngleInRads = 0.8579515,
        angleToItem: AngleInRads = 0.0,
        distanceToItem: Distance = 1.0
): List<AR.Item> {
    return itemsObservable()
            .toPositionOnScreenInternal(
                    pitch = Observable.just(pitch),
                    heading = Observable.just(heading),
                    center = Observable.just(Position(0.0, 0.0)),
                    fieldOfView = Observable.just(AR.FieldOfView(horizontalViewAngle = horizontalViewAngle, verticalViewAngle = verticalViewAngle)),
                    userPositions = Observable.just(Position(52.0, 21.0)),
                    getAngleTo = { angleToItem },
                    getDistanceTo = { distanceToItem }
            ).blockingFirst()
}

private fun itemsObservable(): Observable<Iterable<Game.Item>> {
    return Observable.just(listOf(object : Game.Item {
        override val position = newPosition(52.1, 21.0)
    }))
}