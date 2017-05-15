package com.indoorway.coffeehunt.game.ar

import android.graphics.*
import io.reactivex.Observable
import com.indoorway.coffeehunt.R
import com.indoorway.coffeehunt.game.DI
import com.indoorway.coffeehunt.game.core.Game

typealias PitchEvents = Observable<AngleInRads>

typealias HeadingEvents = Observable<AngleInRads>

typealias AngleInRads = Double

typealias Distance = Double

object AR {

    private val resources by lazy { DI.provideApplicationContext().resources }
    private val regularSeedBitmap by lazy { BitmapFactory.decodeResource(resources, R.drawable.regular_seed) }
    private val specialSeedBitmap by lazy { BitmapFactory.decodeResource(resources, R.drawable.special_seed) }
    private val monsterBitmap by lazy { BitmapFactory.decodeResource(resources, R.drawable.coffee_monster) }


    interface View {
        fun showItems(items: List<Item>)
    }

    interface Item {
        val relativePositionOnScreen: RelativePositionOnScreen
        fun draw(canvas: Canvas)
    }

    class InvisibleItem(override val relativePositionOnScreen: RelativePositionOnScreen) : Item {
        override fun draw(canvas: Canvas) = Unit
    }

    class RegularSeed(override val relativePositionOnScreen: RelativePositionOnScreen) : Item {
        override fun draw(canvas: Canvas) = AR.draw(canvas, regularSeedBitmap, relativePositionOnScreen)
    }

    class SpecialSeed(override val relativePositionOnScreen: RelativePositionOnScreen) : Item {
        override fun draw(canvas: Canvas) = AR.draw(canvas, specialSeedBitmap, relativePositionOnScreen)
    }

    class Monster(override val relativePositionOnScreen: RelativePositionOnScreen) : Item {
        override fun draw(canvas: Canvas) = AR.draw(canvas, monsterBitmap, relativePositionOnScreen)
    }

    fun draw(canvas: Canvas, bitmap: Bitmap, relativePositionOnScreen: RelativePositionOnScreen) {
        val cx = (canvas.width / 2f).let { it - relativePositionOnScreen.width * it }
        val cy = (canvas.height / 2f).let { it - relativePositionOnScreen.height * it }
        val left = (cx - relativePositionOnScreen.size * bitmap.width / 2).toInt()
        val top = (cy - relativePositionOnScreen.size * bitmap.height / 2).toInt()
        val right = (cx + relativePositionOnScreen.size * bitmap.width / 2).toInt()
        val bottom = (cy + relativePositionOnScreen.size * bitmap.height / 2).toInt()
        canvas.drawBitmap(bitmap, null, Rect(left, top, right, bottom), paint.apply { alpha = relativePositionOnScreen.alpha })
    }

    data class RelativePositionOnScreen(val width: Float, val height: Float, val size: Float, val alpha: Int)

    data class FieldOfView(
            val horizontalViewAngle: AngleInRads,
            val verticalViewAngle: AngleInRads)

    private val paint = Paint()
}

fun Game.Item.toARItem(relativePositionOnScreen: AR.RelativePositionOnScreen) = when(this) {
    is Game.Seed.Regular -> AR.RegularSeed(relativePositionOnScreen)
    is Game.Seed.Special -> AR.SpecialSeed(relativePositionOnScreen)
    is Game.Monster -> AR.Monster(relativePositionOnScreen)
    else -> AR.InvisibleItem(relativePositionOnScreen)
}

