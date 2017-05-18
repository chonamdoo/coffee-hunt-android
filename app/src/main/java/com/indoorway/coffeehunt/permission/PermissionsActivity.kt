package com.indoorway.coffeehunt.permission

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.indoorway.coffeehunt.R
import com.indoorway.coffeehunt.game.CompassCalibrationActivity
import com.indoorway.coffeehunt.permission.DI.providePermissionsIteractor
import kotlinx.android.synthetic.main.permissions_activity.*

class PermissionsActivity : AppCompatActivity() {

    private val permissionsController = PermissionsController(
            providePermissionsIteractor(this),
            { openCompassCalibrationActivity() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.permissions_activity)
        permissionsController.init()
        grantPermissionsButton.setOnClickListener {
            permissionsController.showRequestPermissionsDialog()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsController.onRequestPermissionsResult(requestCode, grantResults)
    }

    private fun openCompassCalibrationActivity() {
        startActivity(Intent(this, CompassCalibrationActivity::class.java))
        finish()
    }
}