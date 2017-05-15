package com.indoorway.coffeehunt.game.ar

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

class ARViewImpl @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0)
    : View(context, attrs, defStyleAttr, defStyleRes), AR.View {

    var items = emptyList<AR.Item>()

    override fun onDraw(canvas: Canvas) {
        items.sortedBy { it.relativePositionOnScreen.size }.forEach { it.draw(canvas) }
        super.onDraw(canvas)
    }
    override fun showItems(items: List<AR.Item>) {
        this.items = items
        invalidate()
    }
}