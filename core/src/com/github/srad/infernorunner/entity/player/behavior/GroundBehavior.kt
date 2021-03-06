package com.github.srad.infernorunner.entity.player.behavior

import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector3
import com.github.srad.infernorunner.core.GameInfo
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.player.PlayerEntity
import com.github.srad.infernorunner.entity.behavior.Behavior

class GroundBehavior(parent: PlayerEntity) : Behavior<PlayerEntity>(parent) {
    override fun handleInput(gameInfo: GameInfo, delta: Float) {
        if (gameInfo.key.pressed(Input.Keys.SPACE) || gameInfo.controller.a) {
            parent.rigidBody.linearVelocity = Vector3(0f, parent.playerSettings.jumpVelocity, 0f)
            Resource.jumpSound.load.play()
            parent.listener.jump()
            parent.behaviorManager.behavior = PlayerBehavior.Air
        }
    }
}