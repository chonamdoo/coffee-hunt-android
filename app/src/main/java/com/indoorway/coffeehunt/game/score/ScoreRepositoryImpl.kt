package com.indoorway.coffeehunt.game.score

import android.preference.PreferenceManager
import com.elpassion.android.commons.sharedpreferences.createSharedPrefs
import com.google.gson.Gson
import com.indoorway.coffeehunt.game.DI

class ScoreRepositoryImpl : Score.Repository {

    private val repository = createSharedPrefs<Int?>({
        PreferenceManager.getDefaultSharedPreferences(DI.provideApplicationContext())
    }, { Gson() })

    override var bestScore: Int?
        get() = repository.read(BEST_SCORE_KEY)
        set(value) {
            repository.write(BEST_SCORE_KEY, value)
        }

    companion object {
        private val BEST_SCORE_KEY = "best_score"
    }
}