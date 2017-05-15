package com.indoorway.coffeehunt.login

class LoginController(private val view: Login.View,
                      private val repository: Login.Repository,
                      private val indoorwayInitializer: IndoorwayInitializer) {

    fun onCreate() {
        repository.token?.let {
            indoorwayInitializer.init(it)
            view.openPermissionsScreen()
        }
    }

    fun onToken(token: String) {
        indoorwayInitializer.init(token)
        repository.token = token
        view.openPermissionsScreen()
    }
}