package com.indoorway.coffeehunt.permission

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.permissions_activity.*
import com.indoorway.coffeehunt.R
import com.indoorway.coffeehunt.game.GameActivity
import com.indoorway.coffeehunt.permission.DI.providePermissionsIteractor

class PermissionsActivity : AppCompatActivity() {

    private val permissionsController = PermissionsController(
            providePermissionsIteractor(this),
            { openGameScreen() })

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

    private fun openGameScreen() {
        startActivity(Intent(this, GameActivity::class.java))
        finish()
    }
}