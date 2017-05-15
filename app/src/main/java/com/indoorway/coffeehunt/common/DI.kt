package com.indoorway.coffeehunt.common

import com.indoorway.coffeehunt.login.Login
import com.indoorway.coffeehunt.login.LoginRepositoryImpl

object DI {

    var provideLoginRepository: () -> Login.Repository = { LoginRepositoryImpl() }
}