package com.github.srad.infernorunner.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.github.srad.infernorunner.core.CollisionMasks
import com.github.srad.infernorunner.core.IDamageMaker
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.player.IPlayerUpdateListener
import com.github.srad.infernorunner.entity.player.PlayerInstance
import com.github.srad.infernorunner.level.IMappable
import com.github.srad.infernorunner.level.MapInfo

class SpiderInstance : PhysicalModelInstance(Resource.spiderModel, PhysicalAttributes(btBoxShape(Vector3(1f, .5f, 1f)), 0f, CollisionMasks(PlayerInstance::class))), IMappable, IPlayerUpdateListener, IDamageMaker {
    override val name = "Spider"
    override val mapInfo = MapInfo("Enemy", Color.PURPLE)
    override val damage: Int
        get() {
            if (!hitPlayer) {
                hitPlayer = true
                return 1
            }
            return 0
        }

    var hitPlayer = false

    override fun updatePlayer(delta: Float, playerInstance: PlayerInstance) {
        val me = this
        val target = playerInstance

        val towards = target.translation.sub(me.translation).sub(0f, 2f, 0f).scl(delta * 0.3f)
        if (towards.len2() > 0.00005f) {
            rigidBody.translate(towards)
        }
    }
}