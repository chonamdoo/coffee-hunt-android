package com.indoorway.coffeehunt.game.minimap

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import com.indoorway.android.common.sdk.listeners.generic.Action1
import com.indoorway.android.common.sdk.model.Coordinates
import com.indoorway.android.map.sdk.view.IndoorwayMapView
import com.indoorway.android.map.sdk.view.drawable.figures.DrawableCircle
import com.indoorway.android.map.sdk.view.drawable.figures.DrawableIcon
import com.indoorway.android.map.sdk.view.drawable.layers.MarkersLayer
import com.indoorway.android.map.sdk.view.drawable.layers.ModificationsCollection
import com.indoorway.android.map.sdk.view.drawable.textures.BitmapTexture
import com.indoorway.coffeehunt.R
import com.indoorway.coffeehunt.game.core.Game

class MiniMapView : IndoorwayMapView {

    companion object {
        private val TEXTURE_POINTER = "TEXTURE_POINTER"
        private val TEXTURE_MONSTER = "TEXTURE_MONSTER"
        private val ID_PLAYER = "PLAYER"
    }

    private val pointerTexture by lazy { BitmapTexture(TEXTURE_POINTER, getPointerBitmap()) }
    private val monsterTexture by lazy { BitmapTexture(TEXTURE_MONSTER, getMonsterBitmap()) }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean = true

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return true
    }

    private var monstersLayer: MarkersLayer? = null

    private var seedsLayer: MarkersLayer? = null

    private var playerLayer: MarkersLayer? = null

    fun initialize(buildingUUID: String, mapUUID: String, onMapLoaded: () -> Unit) {
        onMapLoadCompletedListener = Action1 {
            seedsLayer = markerControl.addLayer(11.0f)
            monstersLayer = markerControl.addLayer(12.0f).apply {
                applyModifications(ModificationsCollection().noRotation())
            }
            playerLayer = markerControl.addLayer(13.0f).apply {
                applyModifications(ModificationsCollection().noRotation())
            }
            invalidate()
            onMapLoaded()
            cameraControl.run {
                val scale = 0.17f
                setMaxScale(scale)
                setScale(scale)
            }
            playerLayer?.registerTexture(pointerTexture)
            monstersLayer?.registerTexture(monsterTexture)
        }
        loadMap(buildingUUID, mapUUID)
    }

    fun removePlayer() {
        playerLayer?.remove(ID_PLAYER)
    }

    fun addPlayer(player: Game.Player.Existent) {
        cameraControl.setPosition(player.getCoordinates())
        playerLayer?.add(providePlayerDrawable(player.getCoordinates()))
    }

    private fun providePlayerDrawable(coordinates: Coordinates)
            = DrawableIcon(ID_PLAYER, TEXTURE_POINTER, coordinates, sizeX = 1.5f, sizeY = 1.5f)

    fun addSeed(seed: Game.Seed) {
        seedsLayer?.add(drawableForSeed(seed))
    }

    fun removeSeed(seedKey: String) {
        seedsLayer?.remove(seedKey)
    }

    fun addMonster(monster: Game.Monster) {
        monstersLayer?.add(drawableForMonster(monster))
    }

    fun removeMonster(monsterKey: String) {
        monstersLayer?.remove(monsterKey)
    }

    private fun drawableForMonster(monster: Game.Monster)
            = DrawableIcon(monster.key, TEXTURE_MONSTER, monster.getCoordinates(), sizeX = 1.0f, sizeY = 1.0f)

    private fun drawableForSeed(seed: Game.Seed)
            = DrawableCircle(seed.key, 0.3f, seed.getColor(), seed.getColor(), 0f, seed.getCoordinates())


    private fun getPointerBitmap() = BitmapFactory.decodeResource(context.resources, R.drawable.pointer)

    private fun getMonsterBitmap() = BitmapFactory.decodeResource(context.resources, R.drawable.monster)

    private fun Game.Seed.getColor() = if (this is Game.Seed.Regular) Color.parseColor("#9B5700") else Color.parseColor("#CA6F12")

    private fun Game.Item.getCoordinates() = Coordinates(position.latitude, position.longitude)

}