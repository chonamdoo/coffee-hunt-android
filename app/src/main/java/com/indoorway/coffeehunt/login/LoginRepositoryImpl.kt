package com.indoorway.coffeehunt.login

import android.preference.PreferenceManager
import com.elpassion.android.commons.sharedpreferences.createSharedPrefs
import com.google.gson.Gson
import com.indoorway.coffeehunt.game.DI

class LoginRepositoryImpl : Login.Repository {

    private val repository = createSharedPrefs<String?>({
        PreferenceManager.getDefaultSharedPreferences(DI.provideApplicationContext())
    }, { Gson() })

    override var token: String?
        get() = repository.read(TOKEN_KEY)
        set(value) {
            repository.write(TOKEN_KEY, value)
        }

    companion object {
        private val TOKEN_KEY = "token"
    }
}