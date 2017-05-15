package com.indoorway.coffeehunt.permission

import android.app.Activity

object DI {

    var providePermissionsIteractor: (Activity) -> PermissionsInteractor = { PermissionsInteractorImpl(it) }
}