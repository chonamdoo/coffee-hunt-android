package com.indoorway.coffeehunt.game.logout

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import com.indoorway.coffeehunt.login.Login

class LogoutControllerTest {

    private val view = mock<LogoutView>()
    private val repository = mock<Login.Repository>()
    private val controller = LogoutController(view, repository)

    @Test
    fun shouldRemoveTokenOnSignOut() {
        controller.signOut()
        verify(repository).token = null
    }

    @Test
    fun shouldOpenLoginScreenOnSignOut() {
        controller.signOut()
        verify(view).openLoginScreen()
    }
}