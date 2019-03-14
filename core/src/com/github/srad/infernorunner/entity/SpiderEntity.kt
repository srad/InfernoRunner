package com.github.srad.infernorunner.entity

import com.badlogic.gdx.graphics.Color
import com.github.srad.infernorunner.core.CollisionMasks
import com.github.srad.infernorunner.core.IDamageMaker
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.base.AbstractPhysicalEntity
import com.github.srad.infernorunner.entity.base.BoxShape
import com.github.srad.infernorunner.entity.base.Mass
import com.github.srad.infernorunner.entity.base.PhysicalAttributes
import com.github.srad.infernorunner.entity.player.IPlayerUpdateListener
import com.github.srad.infernorunner.entity.player.PlayerEntity
import com.github.srad.infernorunner.level.IMappable
import com.github.srad.infernorunner.level.MapInfo

class SpiderEntity : AbstractPhysicalEntity(Resource.spiderModel, PhysicalAttributes(BoxShape(1f, .5f, 1f), Mass(0f), CollisionMasks(PlayerEntity::class))),
        IMappable,
        IPlayerUpdateListener,
        IDamageMaker {

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

    override fun updatePlayer(delta: Float, playerEntity: PlayerEntity) {
        val me = this
        val target = playerEntity

        val towards = target.translation.sub(me.translation).sub(0f, 2f, 0f).scl(delta * 0.3f)
        if (towards.len2() > 0.00005f) {
            rigidBody.translate(towards)
        }
    }
}