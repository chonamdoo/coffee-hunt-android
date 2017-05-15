package com.indoorway.coffeehunt.game.core

import com.indoorway.coffeehunt.game.ar.AngleInRads

data class Position(val latitude: Double, val longitude: Double)

data class FullPosition(val latitude: Double, val longitude: Double, val xDistance: Double, val yDistance: Double)

fun Position.toFull(center: Position): FullPosition {
    return FullPosition(
            latitude,
            longitude,
            getDistanceBetween(latitude, longitude, latitude, center.longitude),
            getDistanceBetween(latitude, longitude, center.latitude, longitude))
}

const val EARTH_RADIUS_IN_METERS = 6_371_000.0

fun FullPosition.getDistanceTo(that: FullPosition): Double {
    return Math.sqrt(((xDistance - that.xDistance) * (xDistance - that.xDistance)) + ((yDistance - that.yDistance) * (yDistance - that.yDistance)))
}

private fun getDistanceBetween(firstLatitude: Double, firstLongitude: Double, secondLatitude: Double, secondLongitude: Double): Double {
    val dLat = Math.toRadians(secondLatitude - firstLatitude)
    val dLng = Math.toRadians(secondLongitude - firstLongitude)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(firstLatitude)) * Math.cos(Math.toRadians(secondLatitude)) *
            Math.sin(dLng / 2) * Math.sin(dLng / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    val dist = (EARTH_RADIUS_IN_METERS * c)
    return dist
}

fun FullPosition.getAngleTo(objectPositions: FullPosition): AngleInRads {
    val deltaLat = (objectPositions.latitude - latitude)
    val deltaLong = (objectPositions.longitude - longitude) * Math.cos(Math.toRadians(latitude))
    return Math.atan2(deltaLong, deltaLat)
}

fun getPositionBetween(p1: FullPosition, p2: FullPosition, progress: Progress) = FullPosition(
        scale(p1.latitude, p2.latitude, progress),
        scale(p1.longitude, p2.longitude, progress),
        scale(p1.xDistance, p2.xDistance, progress),
        scale(p1.yDistance, p2.yDistance, progress)
)

private fun scale(from: Double, to: Double, progress: Int) = from + (to - from) * progress.toDouble() / 100f
