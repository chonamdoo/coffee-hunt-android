package com.indoorway.coffeehunt.game.score

import com.nhaarman.mockito_kotlin.*
import org.junit.Test
import com.indoorway.coffeehunt.game.core.Game
import com.indoorway.coffeehunt.game.rules.newPosition

class ScoreControllerTest {

    private val view = mock<Score.View>()
    private val repository = mock<Score.Repository>()
    private val startNewGameAction: () -> Unit = mock()
    private val controller = ScoreController(view, repository, startNewGameAction)

    @Test
    fun shouldShowFinalScore() {
        val finalScore = Game.Score(listOf(newRegularSeed(), newRegularSeed(), newSpecialSeed()))
        controller.showGameFinalScore(finalScore)
        verify(view).showFinalScore(regularSeeds = 2, specialSeeds = 1, points = 12, bestScoreValue = 12)
    }

    @Test
    fun shouldShowFinalScoreAsBestScoreAfterFirstGame() {
        whenever(repository.bestScore).thenReturn(null)
        controller.showGameFinalScore(finalScore = createScore(50))
        verify(view).showFinalScore(regularSeeds = 50, specialSeeds = 0, points = 50, bestScoreValue = 50)
    }

    @Test
    fun shouldShowSavedScoreAsBestScoreWhenFinalScoreIsLower() {
        whenever(repository.bestScore).thenReturn(1000)
        controller.showGameFinalScore(finalScore = createScore(10))
        verify(view).showFinalScore(any(), any(), any(), eq(1000))
    }

    @Test
    fun shouldShowFinalScoreAsBestScoreWhenSavedScoreIsLower() {
        whenever(repository.bestScore).thenReturn(40)
        controller.showGameFinalScore(finalScore = createScore(120))
        verify(view).showFinalScore(any(), any(), any(), eq(120))
    }

    @Test
    fun shouldSaveFinalScoreAfterFirstGame() {
        whenever(repository.bestScore).thenReturn(null)
        controller.showGameFinalScore(finalScore = createScore(40))
        verify(repository).bestScore = 40
    }

    @Test
    fun shouldSaveFinalScoreWhenSavedScoreIsLower() {
        whenever(repository.bestScore).thenReturn(20)
        controller.showGameFinalScore(finalScore = createScore(50))
        verify(repository).bestScore = 50
    }

    @Test
    fun shouldNotSaveFinalScoreWhenSavedScoreIsHigher() {
        whenever(repository.bestScore).thenReturn(80)
        controller.showGameFinalScore(finalScore = createScore(6))
        verify(repository, never()).bestScore = 60
    }

    @Test
    fun shouldHideFinalScoreOnStartNewGame() {
        controller.onStartNewGame()
        verify(view).hideScore()
    }

    @Test
    fun shouldTriggerStartingNewGameOnStartNewGame() {
        controller.onStartNewGame()
        verify(startNewGameAction).invoke()
    }

    @Test
    fun shouldShowLicencesScreenOnLicences() {
        controller.onLicences()
        verify(view).openLicencesScreen()
    }

    private fun newRegularSeed() = Game.Seed.Regular(newPosition(0.0, 0.0), "")

    private fun newSpecialSeed() = Game.Seed.Special(newPosition(0.0, 0.0), "")

    private fun listOfRegularSeeds(count: Int) = (1..count).map { newRegularSeed() }

    private fun createScore(regularSeedsScore: Int) = Game.Score(listOfRegularSeeds(count = regularSeedsScore))

}