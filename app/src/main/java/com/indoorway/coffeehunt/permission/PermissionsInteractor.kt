package com.indoorway.coffeehunt.permission

import android.app.Activity
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.support.v4.app.ActivityCompat

interface PermissionsInteractor {
    fun shouldCheckPermissions(): Boolean
    fun isPermissionGranted(permission: String): Boolean
    fun showRequestPermissionsDialog(requestCode: Int, vararg permissions: String)
}

class PermissionsInteractorImpl(val activity: Activity) : PermissionsInteractor {
    override fun shouldCheckPermissions() =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    override fun isPermissionGranted(permission: String) =
            ActivityCompat.checkSelfPermission(activity, permission) == PERMISSION_GRANTED

    override fun showRequestPermissionsDialog(requestCode: Int, vararg permissions: String) =
            ActivityCompat.requestPermissions(activity, permissions, requestCode)
}

