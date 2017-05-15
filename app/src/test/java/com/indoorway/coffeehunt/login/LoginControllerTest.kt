package com.indoorway.coffeehunt.login

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test

class LoginControllerTest {

    private val view = mock<Login.View>()
    private val repository = mock<Login.Repository>()
    private val indoorwayInitializer = mock<IndoorwayInitializer>()
    private val controller = LoginController(view, repository, indoorwayInitializer)

    @Test
    fun shouldInitializeIndoorwayWhenUserIsSignedIn() {
        whenever(repository.token).thenReturn("token")
        controller.onCreate()
        verify(indoorwayInitializer).init("token")
    }

    @Test
    fun shouldOpenPermissionsScreenImmediatelyWhenUserIsSignedIn() {
        whenever(repository.token).thenReturn("token")
        controller.onCreate()
        verify(view).openPermissionsScreen()
    }

    @Test
    fun shouldNotOpenPermissionsScreenImmediatelyWhenUserIsNotSignedIn() {
        controller.onCreate()
        verify(view, never()).openPermissionsScreen()
    }

    @Test
    fun shouldInitializeIndoorwayWhenTokenReceived() {
        controller.onToken("token")
        verify(indoorwayInitializer).init("token")
    }

    @Test
    fun shouldSaveReceivedToken() {
        controller.onToken("token")
        verify(repository).token = "token"
    }

    @Test
    fun shouldOpenPermissionsScreenWhenTokenReceived() {
        controller.onToken("token")
        verify(view).openPermissionsScreen()
    }
}