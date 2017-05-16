package com.indoorway.coffeehunt.game.minimap

import com.indoorway.coffeehunt.game.core.Game
import com.indoorway.coffeehunt.game.core.Game.createEmptyState

class GameMapView(private val map: MiniMapView) {

    private var displayedState = createEmptyState()

    init {
        map.initialize {
            display(createEmptyState())
            display(displayedState)
        }
    }

    fun display(state: Game.State) {
        updateUserPosition(state)
        updateMonsters(displayedState.monsters, state.monsters)
        updateSeeds(displayedState.seeds, state.seeds)
        displayedState = state
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
            map.addPlayer(it)
        }
    }

    private fun updateSeeds(oldSeeds: Set<Game.Seed>, newSeeds: Set<Game.Seed>) {
        if (oldSeeds != newSeeds) {
            (newSeeds - oldSeeds)
                    .forEach(map::addSeed)
            (oldSeeds - newSeeds)
                    .map(Game.Seed::key)
                    .forEach(map::removeSeed)
        }
    }

    private fun updateMonsters(oldMonsters: List<Game.Monster>, newMonsters: List<Game.Monster>) {
        val newMonstersKeys = newMonsters.map(Game.Monster::key)
        oldMonsters
                .map(Game.Monster::key)
                .filterNot { it in newMonstersKeys }
                .forEach(map::removeMonster)
        newMonsters
                .forEach(map::addMonster)
    }

}