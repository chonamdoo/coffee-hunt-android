package com.indoorway.coffeehunt.game

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.indoorway.android.common.sdk.listeners.position.OnPositionChangedListener
import com.indoorway.android.fragments.sdk.map.MapFragment
import com.indoorway.coffeehunt.R
import kotlinx.android.synthetic.main.callibrate_compass_activity.*

class CompassCalibrationActivity : RxActivity(), MapFragment.OnMapFragmentReadyListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.callibrate_compass_activity)

        tvInstruction.setText(R.string.obtaining_your_location)

        btnNext.apply {
            visibility = View.GONE
            setOnClickListener { openGameScreen() }
        }
    }

    override fun onMapFragmentReady(fragment: MapFragment) {
        fragment.positioningServiceConnection.onPositionChangedListener = OnPositionChangedListener {
            tvInstruction.setText(R.string.rotate_map_to_callibrate_compass)
            btnNext.visibility = View.VISIBLE
        }
    }

    private fun openGameScreen() {
        startActivity(Intent(this, GameActivity::class.java))
        finish()
    }

}