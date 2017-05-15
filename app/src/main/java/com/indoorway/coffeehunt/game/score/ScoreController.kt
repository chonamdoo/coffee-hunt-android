package com.indoorway.coffeehunt.game.score

import com.indoorway.coffeehunt.game.core.Game

class ScoreController(
        private val view: Score.View,
        private val repository: Score.Repository,
        private val startNewGame: () -> Unit) {

    fun showGameFinalScore(finalScore: Game.Score) {
        view.showFinalScore(
                regularSeeds = finalScore.seeds.count { it is Game.Seed.Regular },
                specialSeeds = finalScore.seeds.count { it is Game.Seed.Special },
                points = finalScore.points,
                bestScoreValue = getBestScore(finalScore))
    }

    private fun getBestScore(finalScore: Game.Score): Int {
        val savedScore = repository.bestScore
        if (savedScore == null) {
            repository.bestScore = finalScore.points
            return finalScore.points
        } else {
            val highestScore = Math.max(savedScore, finalScore.points)
            repository.bestScore = highestScore
            return highestScore
        }
    }

    fun onStartNewGame() {
        view.hideScore()
        startNewGame()
    }

    fun onLicences() {
        view.openLicencesScreen()
    }
}