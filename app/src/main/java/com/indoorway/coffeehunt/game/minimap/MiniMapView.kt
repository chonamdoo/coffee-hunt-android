package com.indoorway.coffeehunt.game.minimap

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import com.indoorway.android.common.sdk.model.Coordinates
import com.indoorway.android.map.sdk.IndoorwayMapSdk
import com.indoorway.android.map.sdk.view.IndoorwayMapView
import com.indoorway.android.map.sdk.view.drawable.figures.DrawableCircle
import com.indoorway.android.map.sdk.view.drawable.figures.DrawableIcon
import com.indoorway.android.map.sdk.view.drawable.layers.MarkersLayer
import com.indoorway.android.map.sdk.view.drawable.textures.DrawableTexture
import com.indoorway.coffeehunt.R
import com.indoorway.coffeehunt.game.core.Game

class MiniMapView : IndoorwayMapView {

    companion object {
        private val TEXTURE_POINTER = "TEXTURE_POINTER"
        private val TEXTURE_MONSTER = "TEXTURE_MONSTER"
        private val ID_PLAYER = "PLAYER"
    }

    private val pointerTexture by lazy { DrawableTexture(TEXTURE_POINTER, getPointerBitmap()) }
    private val monsterTexture by lazy { DrawableTexture(TEXTURE_MONSTER, getMonsterBitmap()) }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean = true

    override fun onTouchEvent(event: MotionEvent?): Boolean = true

    var monstersLayer: MarkersLayer? = null

    var seedsLayer: MarkersLayer? = null

    var playerLayer: MarkersLayer? = null

    fun initialize(buildingUUID: String, mapUUID: String, onMapLoaded: () -> Unit) {
        customizeColors()
        setOnMapLoadCompletedListener {
            seedsLayer = markerControl.addLayer(11.0f)
            monstersLayer = markerControl.addLayer(12.0f)
            playerLayer = markerControl.addLayer(13.0f)
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
        displayControl.bringToFront()
    }

    private fun customizeColors() {
        IndoorwayMapSdk.getInstance().config.run {
            defaultRoomBackgroundColor = Color.parseColor("#5A5F91")
            defaultRoomOutlineColor = Color.parseColor("#242936")
            textColor = Color.parseColor("#242936")
            textShadowColor = Color.parseColor("#242936")
            mapIndoorBackgroundColor = Color.parseColor("#3C3B47")
            mapOutlineColor = Color.parseColor("#242936")
        }
    }

    fun removePlayer() {
        playerLayer?.remove(ID_PLAYER)
    }

    fun addPlayer(player: Game.Player.Existent) {
        cameraControl.setPosition(player.getCoordinates())
        playerLayer?.add(providePlayerDrawable(player.getCoordinates()))
    }

    private fun providePlayerDrawable(coordinates: Coordinates)
            = DrawableIcon(ID_PLAYER, TEXTURE_POINTER, coordinates, 1.5f, 1.5f)

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
            = DrawableIcon(monster.key, TEXTURE_MONSTER, monster.getCoordinates(), 1.0f, 1.0f)

    private fun drawableForSeed(seed: Game.Seed)
            = DrawableCircle(seed.key, 0.3f, seed.getColor(), seed.getColor(), 0f, seed.getCoordinates())


    private fun getPointerBitmap() = BitmapFactory.decodeResource(context.resources, R.drawable.pointer)

    private fun getMonsterBitmap() = BitmapFactory.decodeResource(context.resources, R.drawable.monster)

    private fun Game.Seed.getColor() = if (this is Game.Seed.Regular) Color.parseColor("#9B5700") else Color.parseColor("#CA6F12")

    private fun Game.Item.getCoordinates() = Coordinates(position.latitude, position.longitude)

}