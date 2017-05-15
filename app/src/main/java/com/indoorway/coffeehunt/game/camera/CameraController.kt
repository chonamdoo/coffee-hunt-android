package com.indoorway.coffeehunt.game.camera

class CameraController(private val view: LiveStream.View) {

    private var cameraWrapper: LiveStream.CameraWrapper? = null

    fun startLiveStream(cameraWrapper: LiveStream.CameraWrapper) {
        this.cameraWrapper = cameraWrapper
        try {
            view.showLiveStreamFromCamera(cameraWrapper.createCameraView())
        } catch (e: Exception) {
            view.showCameraError()
        }
    }

    fun stopLiveStream() {
        cameraWrapper?.releaseCameraView()
        cameraWrapper = null
    }
}