package com.indoorway.coffeehunt.game.logout

import com.indoorway.coffeehunt.login.Login

class LogoutController(private val view: LogoutView,
                       private val repository: Login.Repository) {

    fun signOut() {
        repository.token = null
        view.openLoginScreen()
    }
}