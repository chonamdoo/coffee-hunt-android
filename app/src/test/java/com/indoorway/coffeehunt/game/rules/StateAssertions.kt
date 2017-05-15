package com.indoorway.coffeehunt.game.rules

import org.junit.Assert
import com.indoorway.coffeehunt.game.core.Game

fun Game.State.assertPlayerPosition(latitude: Double, longitude: Double) = apply {
    Assert.assertTrue("Player does not exist", player is Game.Player.Existent)
    Assert.assertEquals("Player has incorrect latitude", latitude, (player as Game.Player.Existent).position.latitude, 0.0000001)
    Assert.assertEquals("Player has incorrect longitude", longitude, (player as Game.Player.Existent).position.longitude, 0.0000001)
}

fun Game.State.assertPlayerDead() = apply {
    Assert.assertTrue("Player is not dead. $this", player is Game.Player.Existent.Dead)
}

fun Game.State.assertPlayerAlive() = apply {
    Assert.assertTrue("Player is not alive. $this", player is Game.Player.Existent.Alive)
}

fun Game.State.assertPlayerScore(expectedScore: Int) = apply {
    Assert.assertEquals("Player's score does not match", expectedScore, player.score.points)
}

fun Game.State.assertMonsters(expectedMonsters: List<Game.Monster>) = apply {
    Assert.assertEquals("Monster does not match", expectedMonsters, this.monsters)
}

fun Game.State.assertSeeds(expectedSeeds: Set<Any>) = apply {
    Assert.assertEquals("Seeds does not match", expectedSeeds, this.seeds)
}

fun Game.State.assertBoard(expectedBoard: Game.Board) = apply {
    Assert.assertEquals("Board does not match", expectedBoard, this.board)
}