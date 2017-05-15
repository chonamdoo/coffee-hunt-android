package com.indoorway.coffeehunt.game.camera

import android.content.Context
import android.hardware.Camera
import io.reactivex.Observable
import io.reactivex.Observable.just
import com.indoorway.coffeehunt.game.ar.AR
import com.indoorway.coffeehunt.game.camera.LiveStream.CameraWrapper

class CameraWrapperImpl(val context: Context, val camera: Camera) : CameraWrapper {

    override fun createCameraView() = CameraLiveStream(context, camera)

    override fun releaseCameraView() {
        camera.setPreviewCallback(null)
        camera.stopPreview()
        camera.release()
    }

    override fun getFieldOfView(): Observable<AR.FieldOfView> {
        return just(camera.parameters.run {
            AR.FieldOfView(
                    horizontalViewAngle = Math.toRadians(horizontalViewAngle.toDouble()),
                    verticalViewAngle = Math.toRadians(verticalViewAngle.toDouble()))
        })
    }
}