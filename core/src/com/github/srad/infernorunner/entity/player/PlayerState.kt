package com.github.srad.infernorunner.entity.player

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.TimeUtils
import com.github.srad.infernorunner.core.GameInfo
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.state.State

enum class PlayerState {
    Ground, Air, LevelCompleted, Dead, GameOver, Void, Won, LevelCompletedState, StatusState
}

class LevelCompletedState(parent: PlayerInstance) : State<PlayerInstance>(parent) {
    private var levelCompleteTime = 0L

    override fun enter() {
        levelCompleteTime = TimeUtils.millis()
        Resource.comeHere.load.play()
    }

    override fun update(delta: Float) {
        if (TimeUtils.timeSinceMillis(levelCompleteTime) > 7) {
            parent.stateManager.state = PlayerState.Ground
        }
    }
}

class MoveState(parent: PlayerInstance) : State<PlayerInstance>(parent) {
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
            parent.stateManager.state = PlayerState.Dead
        }
    }
}

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

class GroundState(parent: PlayerInstance) : State<PlayerInstance>(parent) {
    override fun handleInput(gameInfo: GameInfo, delta: Float) {
        if (gameInfo.key.pressed(Input.Keys.SPACE) || gameInfo.controller.a) {
            parent.rigidBody.linearVelocity = Vector3(0f, parent.playerSettings.jumpVelocity, 0f)
            Resource.jumpSound.load.play()
            parent.listener.jump()
            parent.stateManager.state = PlayerState.Air
        }
    }
}

/** Enables mouse and joystick look-around. */
class LookAroundState(parent: PlayerInstance) : State<PlayerInstance>(parent) {
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

class DeadState(parent: PlayerInstance) : State<PlayerInstance>(parent) {
    private var deathTime = 0L
    private var isDead = false

    override fun enter() {
        isDead = false
        parent.applyDamage()
        deathTime = TimeUtils.millis()
        isDead = true

        parent.listener.death()

        if (parent.lives > 0) {
            parent.damage(-1)
        }
    }

    override fun update(delta: Float) {
        if (TimeUtils.timeSinceMillis(deathTime) < 2000) {
            return
        } else {
            parent.respawn = true
            parent.stateManager.state = PlayerState.Ground
            Resource.screamSound.load.stop()
        }
    }
}

class GameOverState(parent: PlayerInstance) : State<PlayerInstance>(parent)
class VoidState(parent: PlayerInstance) : State<PlayerInstance>(parent)
