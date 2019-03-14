package com.github.srad.infernorunner.entity.player.behavior

import com.badlogic.gdx.utils.TimeUtils
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.player.PlayerEntity
import com.github.srad.infernorunner.entity.behavior.Behavior

class DeadBehavior(parent: PlayerEntity) : Behavior<PlayerEntity>(parent) {
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
            parent.behaviorManager.behavior = PlayerBehavior.Ground
            Resource.screamSound.load.stop()
        }
    }
}