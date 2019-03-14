package com.github.srad.infernorunner.entity

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.github.srad.infernorunner.core.*
import com.github.srad.infernorunner.entity.animation.RotationAnimation
import com.github.srad.infernorunner.entity.animation.VerticalAnimator
import com.github.srad.infernorunner.entity.base.AbstractPhysicalEntity
import com.github.srad.infernorunner.entity.base.Mass
import com.github.srad.infernorunner.entity.base.PhysicalAttributes
import com.github.srad.infernorunner.entity.player.PlayerEntity
import kotlin.reflect.KClass

class LifeEntity : AbstractPhysicalEntity(Resource.lifeModel, PhysicalAttributes(btBoxShape(Vector3(.3f, 1f, .3f)), Mass(0f), CollisionMasks(PlayerEntity::class))), IScoreGiver, IDestroyable, IHealthGiver {
    override val name = "Life"
    override val score: Int = 1
    override val health: Int = 1
    override val destroyedBy: Array<KClass<*>> = arrayOf(PlayerEntity::class)

    override fun create() {
        super.create()
        addController(RotationAnimation::class)
        addController(VerticalAnimator::class)
    }
}