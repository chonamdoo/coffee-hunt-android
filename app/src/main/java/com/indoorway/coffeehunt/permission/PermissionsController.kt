package com.indoorway.coffeehunt.permission

import android.Manifest
import android.support.v4.content.PermissionChecker.PERMISSION_GRANTED

class PermissionsController(
        private val permissionsInteractor: PermissionsInteractor,
        private val onPermissionsGranted: () -> Unit) {

    private val RC_REQ_PERMS = 54324

    fun init() {
        if (permissionsInteractor.shouldCheckPermissions()) {
            checkPermissions()
        } else {
            onPermissionsGranted()
        }
    }

    private fun checkPermissions() {
        if (permissionsInteractor.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)
                && permissionsInteractor.isPermissionGranted(Manifest.permission.CAMERA)) {
            onPermissionsGranted()
        }
    }

    fun showRequestPermissionsDialog() {
        return permissionsInteractor.showRequestPermissionsDialog(RC_REQ_PERMS, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == RC_REQ_PERMS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PERMISSION_GRANTED }) {
                onPermissionsGranted()
            }
        }
    }
}