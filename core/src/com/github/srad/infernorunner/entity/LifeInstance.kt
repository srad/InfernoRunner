package com.github.srad.infernorunner.entity

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.github.srad.infernorunner.core.*
import com.github.srad.infernorunner.entity.player.PlayerInstance
import kotlin.reflect.KClass

class LifeInstance : PhysicalModelInstance(Resource.lifeModel, PhysicalAttributes(btBoxShape(Vector3(.3f, 1f, .3f)), 0f, CollisionMasks(PlayerInstance::class))), IScoreGiver, IDestroyable, IHealthGiver {
    override val name = "Life"
    override val score: Int = 1
    override val health: Int = 1
    override val destroyedBy: Array<KClass<*>> = arrayOf(PlayerInstance::class)

    override fun create() {
        super.create()
        addController(RotationAnimation::class)
        addController(VerticalAnimator::class)
    }
}