package com.github.srad.infernorunner.entity.player.behavior

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector3
import com.github.srad.infernorunner.core.GameInfo
import com.github.srad.infernorunner.entity.player.PlayerEntity
import com.github.srad.infernorunner.entity.behavior.Behavior

/** Enables mouse and joystick look-around. */
class LookAroundState(parent: PlayerEntity) : Behavior<PlayerEntity>(parent) {
    override fun handleInput(gameInfo: GameInfo, delta: Float) {
        val cam = parent.cam

        val controller = gameInfo.controller
        val camDir = cam.direction.cpy()

        var deltaX = -Gdx.input.deltaX * parent.playerSettings.horizontalRotationVelocity
        var deltaY = -Gdx.input.deltaY * parent.playerSettings.verticalRotationVelocity

        if (controller.analogRight.horizontal.active) {
            deltaX = -controller.analogRight.horizontal.scaleWithSign * 2f
        }
        if (controller.analogRight.vertical.active) {
            deltaY = -controller.analogRight.vertical.scaleWithSign * 3.4f
        }

        val rotateUp = camDir.cpy().crs(cam.up)
        cam.direction.rotate(rotateUp.nor(), deltaY * parent.playerSettings.verticalRotationVelocity)
        cam.rotate(Vector3.Y, deltaX)
        parent.rigidBody.worldTransform = parent.rigidBody.worldTransform.rotate(Vector3.Y, deltaX)
    }
}