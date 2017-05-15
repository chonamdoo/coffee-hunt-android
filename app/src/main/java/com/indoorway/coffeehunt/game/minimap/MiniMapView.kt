package com.indoorway.coffeehunt.game.minimap

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.indoorway.android.map.sdk.view.IndoorwayMapView

class MiniMapView : IndoorwayMapView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean = true

    override fun onTouchEvent(event: MotionEvent?): Boolean = true
}