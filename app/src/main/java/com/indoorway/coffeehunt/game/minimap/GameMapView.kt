package com.indoorway.coffeehunt.game.minimap

import android.graphics.Color
import com.indoorway.android.common.sdk.model.Coordinates
import com.indoorway.android.map.sdk.IndoorwayMapSdk
import com.indoorway.coffeehunt.game.core.Game
import com.indoorway.coffeehunt.game.core.Position

class GameMapView(private val map: MiniMapView) {

    private var ready = false
    private var lastState: Game.State? = null
    private var displayedState = Game.State(Game.Player.None, emptyList(), emptySet(), Game.Board(Position(0.0, 0.0), emptyMap()))

    init {
        customizeMapColors()
        map.initialize {
            ready = true
            lastState?.let { display(it) }
        }
    }

    private fun customizeMapColors() {
        IndoorwayMapSdk.getInstance().config.run {
            defaultRoomBackgroundColor = Color.parseColor("#5A5F91")
            defaultRoomOutlineColor = Color.parseColor("#242936")
            textColor = Color.parseColor("#242936")
            textShadowColor = Color.parseColor("#242936")
            mapIndoorBackgroundColor = Color.parseColor("#3C3B47")
            mapOutlineColor = Color.parseColor("#242936")
        }
    }

    fun display(state: Game.State) {
        lastState = state
        if (ready) {
            updateUserPosition(state)
            updateMonsters(displayedState.monsters, state.monsters)
            updateSeeds(displayedState.seeds, state.seeds)
            displayedState = state
        }
    }

    private fun updateUserPosition(state: Game.State) {
        if (state.player != displayedState.player) {
            removePlayerFromMap()
            addPlayerToMap(state)
        }
    }

    private fun removePlayerFromMap() {
        if (displayedState.player is Game.Player.Existent) {
            map.removePlayer()
        }
    }

    private fun addPlayerToMap(state: Game.State) {
        (state.player as? Game.Player.Existent)?.let {
            map.cameraControl.setPosition(Coordinates(it.position.latitude, it.position.longitude))
            map.addPlayer(it)
        }
    }

    private fun updateSeeds(oldSeeds: Set<Game.Seed>, newSeeds: Set<Game.Seed>) {
        if (oldSeeds != newSeeds) {
            (newSeeds - oldSeeds)
                    .forEach { map.addSeed(it) }
            (oldSeeds - newSeeds)
                    .map(Game.Seed::key)
                    .forEach { map.removeSeed(it) }
        }
    }

    private fun updateMonsters(oldMonsters: List<Game.Monster>, newMonsters: List<Game.Monster>) {
        oldMonsters
                .map(Game.Monster::key)
                .filterNot { it in newMonsters.map(Game.Monster::key) }
                .forEach { map.removeMonster(it) }
        newMonsters
                .forEach { map.addMonster(it) }
    }
}