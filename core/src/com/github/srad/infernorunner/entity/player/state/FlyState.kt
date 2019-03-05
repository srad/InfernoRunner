package com.github.srad.infernorunner.entity.player.state

import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector3
import com.github.srad.infernorunner.core.GameInfo
import com.github.srad.infernorunner.entity.player.PlayerInstance
import com.github.srad.infernorunner.entity.state.State

class FlyState(parent: PlayerInstance) : State<PlayerInstance>(parent) {
    private var flyButtonDown = false
    private var isMassZero = false

    override fun handleInput(gameInfo: GameInfo, delta: Float) {
        val key = gameInfo.key
        val controller = gameInfo.controller

        if (!controller.b) {
            flyButtonDown = false
        }

        if ((controller.b || key.pressed(Input.Keys.F)) && !flyButtonDown) {
            flyButtonDown = true
            isMassZero = if (isMassZero) {
                parent.rigidBody.setMassProps(parent.physics.mass, Vector3.Zero)
                false
            } else {
                parent.rigidBody.setMassProps(0f, Vector3.Zero)
                true
            }
        }
        if (isMassZero) {
            if (controller.l || key.pressed(Input.Keys.Q)) {
                parent.rigidBody.translate(Vector3(0f, 1f, 0f))
            }
            if (controller.r || key.pressed(Input.Keys.E)) {
                parent.rigidBody.translate(Vector3(0f, -1f, 0f))
            }
        }
    }
}