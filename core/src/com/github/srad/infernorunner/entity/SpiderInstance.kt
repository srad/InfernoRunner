package com.github.srad.infernorunner.entity

import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.player.IPlayerUpdateListener
import com.github.srad.infernorunner.entity.player.PlayerInstance

class SpiderInstance : AbstractModelInstance(Resource.spiderModel), IPlayerUpdateListener {
    override val name = "Spider"
    var hitPlayer = false

    override fun updatePlayer(delta: Float, playerInstance: PlayerInstance) {
        val me = this
        val target = playerInstance

        val towards = target.translation.sub(me.translation).sub(0f, 2f, 0f).scl(delta * 0.3f)
        if (towards.len2() > 0.00005f) {
            transform.translate(towards)
        } else {
            // TODO: Just for testing, hit once.
            if (!hitPlayer) {
                hitPlayer = true
                playerInstance.addBloodStain()
            }
        }
    }
}