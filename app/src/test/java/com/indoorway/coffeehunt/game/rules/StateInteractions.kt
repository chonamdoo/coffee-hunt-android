package com.indoorway.coffeehunt.game.rules

import com.indoorway.coffeehunt.game.core.Game
import com.indoorway.coffeehunt.game.core.onPulse
import com.indoorway.coffeehunt.game.core.onUpdatePlayerPosition
import com.indoorway.coffeehunt.game.core.Position

fun Game.State.updatePlayerPosition(latitude: Double, longitude: Double) = onUpdatePlayerPosition(Position(latitude, longitude), this)

fun Game.State.pulse() = onPulse(0L, this)
