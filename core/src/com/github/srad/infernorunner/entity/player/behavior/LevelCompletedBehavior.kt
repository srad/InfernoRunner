package com.github.srad.infernorunner.entity.player.behavior

import com.badlogic.gdx.utils.TimeUtils
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.player.PlayerEntity
import com.github.srad.infernorunner.entity.behavior.Behavior

class LevelCompletedBehavior(parent: PlayerEntity) : Behavior<PlayerEntity>(parent) {
    private var levelCompleteTime = 0L

    override fun enter() {
        levelCompleteTime = TimeUtils.millis()
        Resource.comeHere.load.play()
    }

    override fun update(delta: Float) {
        if (TimeUtils.timeSinceMillis(levelCompleteTime) > 7) {
            parent.behaviorManager.behavior = PlayerBehavior.Ground
        }
    }
}