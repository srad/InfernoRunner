package com.github.srad.infernorunner.entity.player.state

import com.badlogic.gdx.utils.TimeUtils
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.player.PlayerInstance
import com.github.srad.infernorunner.entity.state.State

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