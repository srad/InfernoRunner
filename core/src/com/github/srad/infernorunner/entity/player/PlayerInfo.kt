package com.github.srad.infernorunner.entity.player

import com.badlogic.gdx.graphics.Texture

interface IPlayerListener {
    fun jump() {}
    fun death() {}
    fun gameOver() {}
    fun health() {}
    fun damage() {}
}

interface IPlayerUpdateListener {
    fun updatePlayer(delta: Float, playerInstance: PlayerInstance)
}

data class PlayerSettings(var leftRightVelocity: Float = .25f, var forthBackVelocity: Float = .3f, var verticalRotationVelocity: Float = 0.2f, var horizontalRotationVelocity: Float = 0.15f, var jumpVelocity: Float = 20f, var name: String = "Player 1")

class Blood(val texture: Texture, val x: Float, val y: Float, val w: Float, val h: Float)
