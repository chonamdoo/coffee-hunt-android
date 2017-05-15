package com.indoorway.coffeehunt.common

import com.indoorway.android.common.sdk.model.Coordinates
import com.indoorway.coffeehunt.game.core.Game
import com.indoorway.coffeehunt.game.ar.AR
import com.indoorway.coffeehunt.game.core.FullPosition
import java.util.*

fun Game.Player.toShortString() = when (this) {
    is Game.Player.None -> "P(None)"
    is Game.Player.Existent.Alive -> "P(Alive:${position.toShortString()})"
    is Game.Player.Existent -> "P(Dead:${position.toShortString()})"
}

fun FullPosition.toShortString() = "P(${latitude.toShortString()},${longitude.toShortString()})"

fun Coordinates.toShortString() = "C(${latitude.toShortString()},${longitude.toShortString()})"

fun Double.toShortString() = String.format(Locale.US, "%3.6f", this)

fun Float.toShortString() = String.format(Locale.US, "%3.6f", this)

fun Game.State.toShortString() = "S(P:${player.toShortString()}, ${monsters.toMonstersString()}, FS:${seeds.size})"

fun List<FullPosition>.toShortString() = take(3).joinToString(limit = 3) { it.toShortString() }

fun Game.Monster.toShortString() = "G(${position.toShortString()})"

fun List<Game.Monster>.toMonstersString() = take(3).joinToString(limit = 3) { it.toShortString() }

fun List<AR.Monster>.toARMonstersString() = take(3).joinToString(limit = 3) { it.toShortString() }

fun List<AR.Item>.toARItemString() = take(3).joinToString(limit = 3) { it.toShortString() }

fun AR.Monster.toShortString() = "AR.G(${relativePositionOnScreen.toShortString()})"

fun AR.Item.toShortString() = "AR.D(${relativePositionOnScreen.toShortString()})"

fun AR.RelativePositionOnScreen.toShortString() = "RP(w:${width.toShortString()},h:${height.toShortString()}"

