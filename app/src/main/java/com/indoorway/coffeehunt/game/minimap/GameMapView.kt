package com.indoorway.coffeehunt.game.minimap

import android.graphics.BitmapFactory
import android.graphics.Color
import com.indoorway.android.common.sdk.model.Coordinates
import com.indoorway.android.map.sdk.IndoorwayMapSdk
import com.indoorway.android.map.sdk.view.drawable.figures.DrawableCircle
import com.indoorway.android.map.sdk.view.drawable.figures.DrawableIcon
import com.indoorway.android.map.sdk.view.drawable.layers.MarkersLayer
import com.indoorway.android.map.sdk.view.drawable.textures.DrawableTexture
import com.indoorway.coffeehunt.BuildConfig
import com.indoorway.coffeehunt.R
import com.indoorway.coffeehunt.game.core.Game
import com.indoorway.coffeehunt.game.core.FullPosition
import com.indoorway.coffeehunt.game.core.Position

class GameMapView(private val map: MiniMapView) {

    companion object {
        private val TEXTURE_POINTER = "TEXTURE_POINTER"
        private val TEXTURE_MONSTER = "TEXTURE_MONSTER"
        private val ID_PLAYER = "PLAYER"
    }

    private val pointerTexture by lazy { DrawableTexture(TEXTURE_POINTER, getPointerBitmap()) }
    private val monsterTexture by lazy { DrawableTexture(TEXTURE_MONSTER, getMonsterBitmap()) }

    private lateinit var monstersLayer: MarkersLayer
    private lateinit var SeedsLayer: MarkersLayer
    private lateinit var playerLayer: MarkersLayer

    private var ready = false
    private var lastState: Game.State? = null
    private var displayedState = Game.State(Game.Player.None, emptyList(), emptySet(), Game.Board(Position(0.0, 0.0), emptyMap()))

    init {
        customizeMapColors()
        map.run {
            setOnMapLoadCompletedListener {
                // object layers, from bottom to top
                SeedsLayer = map.markerControl.addLayer(11.0f)
                monstersLayer = map.markerControl.addLayer(12.0f)
                playerLayer = map.markerControl.addLayer(13.0f)
                map.invalidate()
                ready = true
                lastState?.let { display(it) }
                cameraControl.run {
                    val scale = 0.17f
                    setMaxScale(scale)
                    setScale(scale)
                }
                playerLayer.registerTexture(pointerTexture)
                monstersLayer.registerTexture(monsterTexture)
            }
            loadMap(BuildConfig.BUILDING_UUID, BuildConfig.MAP_UUID)
            displayControl.bringToFront()
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
            playerLayer.remove(ID_PLAYER)
        }
    }

    private fun addPlayerToMap(state: Game.State) {
        (state.player as? Game.Player.Existent)?.let {
            map.cameraControl.setPosition(Coordinates(it.position.latitude, it.position.longitude))
            playerLayer.add(providePlayerDrawable(it.position))
        }
    }

    private fun updateSeeds(oldSeeds: Set<Game.Seed>, newSeeds: Set<Game.Seed>) {
        if (oldSeeds != newSeeds) {
            (newSeeds - oldSeeds)
                    .map(this::drawableForSeed)
                    .forEach(SeedsLayer::add)
            (oldSeeds - newSeeds)
                    .map(Game.Seed::key)
                    .forEach(SeedsLayer::remove)
        }
    }

    private fun updateMonsters(oldMonsters: List<Game.Monster>, newMonsters: List<Game.Monster>) {
        oldMonsters
                .map(Game.Monster::key)
                .filterNot { it in newMonsters.map(Game.Monster::key) }
                .forEach(monstersLayer::remove)
        newMonsters
                .map(this::drawableForMonster)
                .forEach(monstersLayer::add)
    }

    private fun getPointerBitmap() = BitmapFactory.decodeResource(map.context.resources, R.drawable.pointer)

    private fun getMonsterBitmap() = BitmapFactory.decodeResource(map.context.resources, R.drawable.monster)

    private fun providePlayerDrawable(position: FullPosition)
            = DrawableIcon(ID_PLAYER, TEXTURE_POINTER, position.toCoordinates(), 1.5f, 1.5f)

    private fun drawableForMonster(monster: Game.Monster)
            = DrawableIcon(monster.key, TEXTURE_MONSTER, monster.position.toCoordinates(), 1.0f, 1.0f)

    private fun drawableForSeed(seed: Game.Seed)
            = DrawableCircle(seed.key, 0.3f, seed.getColor(), seed.getColor(), 0f, seed.position.toCoordinates())

    private fun FullPosition.toCoordinates() = Coordinates(latitude, longitude)

    private fun Game.Seed.getColor() = if (this is Game.Seed.Regular) Color.parseColor("#9B5700") else Color.parseColor("#CA6F12")
}