package com.indoorway.coffeehunt.game.camera

import io.reactivex.Observable
import com.indoorway.coffeehunt.game.ar.AR

interface LiveStream {

    interface View {
        fun showLiveStreamFromCamera(liveStream: android.view.View)
        fun showCameraError()
    }

    interface CameraWrapper {
        fun createCameraView(): android.view.View
        fun releaseCameraView()
        fun getFieldOfView(): Observable<AR.FieldOfView>
    }
}