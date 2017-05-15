package com.indoorway.coffeehunt.game.score

import android.view.View
import android.webkit.WebView
import kotlinx.android.synthetic.main.score_view.view.*
import com.indoorway.coffeehunt.game.core.REGULAR_SEED_POINTS
import com.indoorway.coffeehunt.game.core.SPECIAL_SEED_POINTS

class ScoreView(private val scoreViewContainer: View, private val licencesView: WebView) : Score.View {

    init {
        licencesView.loadUrl("file:///android_res/raw/licences.html")
    }

    override fun showFinalScore(regularSeeds: Int, specialSeeds: Int, points: Int, bestScoreValue: Int) {
        scoreViewContainer.apply {
            visibility = View.VISIBLE
            coffeeGrainsCount.text = "$regularSeeds x ${REGULAR_SEED_POINTS}"
            specialCoffeeGrainsCount.text = "$specialSeeds x ${SPECIAL_SEED_POINTS}"
            totalScore.text = points.toString()
            bestScore.text = bestScoreValue.toString()
        }
    }

    override fun hideScore() {
        scoreViewContainer.visibility = View.GONE
    }

    override fun openLicencesScreen() {
        licencesView.visibility = View.VISIBLE
    }

    fun onBackPressed(superOnBackPressed: () -> Unit) {
        if (licencesView.visibility == View.VISIBLE) {
            licencesView.visibility = View.GONE
        } else {
            superOnBackPressed()
        }
    }

}