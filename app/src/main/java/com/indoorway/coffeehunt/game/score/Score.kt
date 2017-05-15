package com.indoorway.coffeehunt.game.score

interface Score {

    interface View {
        fun showFinalScore(regularSeeds: Int, specialSeeds: Int, points: Int, bestScoreValue: Int)
        fun hideScore()
        fun openLicencesScreen()
    }

    interface Repository {
        var bestScore: Int?
    }
}