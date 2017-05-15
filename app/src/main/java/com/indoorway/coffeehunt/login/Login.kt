package com.indoorway.coffeehunt.login

interface Login {

    interface View {
        fun openPermissionsScreen()
    }

    interface Repository {
        var token: String?
    }
}