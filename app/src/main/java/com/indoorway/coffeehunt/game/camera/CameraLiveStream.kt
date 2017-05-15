package com.indoorway.coffeehunt.game.camera

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Camera
import android.view.*
import android.widget.FrameLayout

@SuppressLint("ViewConstructor")
@Suppress("DEPRECATION")
class CameraLiveStream(context: Context, private val camera: Camera) : FrameLayout(context), SurfaceHolder.Callback {

    private val surfaceView = SurfaceView(context)
    private val holder = surfaceView.holder
    private var streamSize: Camera.Size? = null

    init {
        addView(surfaceView)
        holder.addCallback(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        val parentHeight = MeasureSpec.getSize(heightMeasureSpec)
        streamSize = getOptimalStreamSize(parentWidth, parentHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (changed && childCount > 0) {
            val width = right - left
            val height = bottom - top
            val streamWidth = streamSize?.height ?: width
            val streamHeight = streamSize?.width ?: height
            val child = getChildAt(0)
            if (width * streamHeight > streamWidth * height) {
                val scaledChildHeight = width * streamHeight / streamWidth
                val offset = (scaledChildHeight - height) / 2
                child.layout(0, -offset, width, height + offset)
            } else {
                val scaledChildWidth = streamWidth * height / streamHeight
                val offset = (scaledChildWidth - width) / 2
                child.layout(-offset, 0, width + offset, height)
            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) = camera.run {
        setPreviewDisplay(holder)
        startPreview()
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        if (holder != null) {
            camera.prepare(holder)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) = Unit

    private fun getOptimalStreamSize(width: Int, height: Int): Camera.Size {
        val sizes = camera.parameters.supportedPreviewSizes
        var optimalSize = sizes.first()
        sizes.drop(1).forEach { size ->
            if (size.width <= width && size.height <= height) {
                if (size.getArea() > optimalSize.getArea()) {
                    optimalSize = size
                }
            }
        }
        return optimalSize
    }

    private fun Camera.Size.getArea() = width * height

    private fun Camera.prepare(holder: SurfaceHolder) {
        stopPreview()
        setPreviewDisplay(holder)
        parameters = parameters.apply {
            focusMode = Camera.Parameters.FOCUS_MODE_INFINITY
            streamSize?.let { setPreviewSize(it.width, it.height) }
        }
        setDisplayOrientation()
        startPreview()
    }

    private fun Camera.setDisplayOrientation() {
        val cameraInfo = getCameraInfo()
        val degrees = context.getRotation().toDegrees()
        val result = (cameraInfo.orientation - degrees + 360) % 360
        setDisplayOrientation(result)
    }

    private fun getCameraInfo() = Camera.CameraInfo().also {
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, it)
    }

    private fun Context.getRotation() = (getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.defaultDisplay?.rotation

    private fun Int?.toDegrees() = when (this) {
        Surface.ROTATION_0 -> 0
        Surface.ROTATION_90 -> 90
        Surface.ROTATION_180 -> 180
        Surface.ROTATION_270 -> 270
        else -> 0
    }
}
