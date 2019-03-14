package com.github.srad.infernorunner.entity.player.behavior

import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector3
import com.github.srad.infernorunner.core.GameInfo
import com.github.srad.infernorunner.entity.player.PlayerEntity
import com.github.srad.infernorunner.entity.behavior.Behavior

class MoveState(parent: PlayerEntity) : Behavior<PlayerEntity>(parent) {
    override fun handleInput(gameInfo: GameInfo, delta: Float) {
        val key = gameInfo.key
        val controller = gameInfo.controller
        val camDir = parent.cam.direction.cpy()
        val cam = parent.cam
        val control = controller.analogLeft
        val playerSettings = parent.playerSettings

        // Move towards camera normal-vector
        if (key.pressed(Input.Keys.UP, Input.Keys.W) || control.up) {
            val vMove = camDir.cpy().scl(playerSettings.forthBackVelocity)
            if (control.up) {
                vMove.scl(control.vertical.scale * .6f)
            }
            parent.rigidBody.translate(Vector3(vMove.x, 0f, vMove.z))
        }
        if (key.pressed(Input.Keys.DOWN, Input.Keys.S) || control.down) {
            val vMove = camDir.cpy().scl(-playerSettings.forthBackVelocity)
            if (control.down) {
                vMove.scl(control.vertical.scale * .6f)
            }
            parent.rigidBody.translate(Vector3(vMove.x, 0f, vMove.z))
        }
        // normal-vector x not-searched-vector = the-vector-we-looking-for
        if (key.pressed(Input.Keys.LEFT, Input.Keys.A) || control.left) {
            parent.rigidBody.translate(camDir.cpy().crs(cam.up).nor().scl(-playerSettings.leftRightVelocity).scl(if (control.left) control.horizontal.scale * .5f else 1f))
        }
        if (key.pressed(Input.Keys.RIGHT, Input.Keys.D) || control.right) {
            parent.rigidBody.translate(camDir.cpy().crs(cam.up).nor().scl(playerSettings.leftRightVelocity).scl(if (control.right) control.horizontal.scale * .5f else 1f))
        }
    }

    override fun update(delta: Float) {
        parent.respawn = false

        parent.cam.position.set(parent.rigidBody.worldTransform.getTranslation(Vector3.Zero).cpy().add(0f, 2f, 0f))
        parent.cam.update()

        if (parent.rigidBody.worldTransform.getTranslation(Vector3.Zero).y < -30f) {
            parent.behaviorManager.behavior = PlayerBehavior.Dead
        }
    }
}