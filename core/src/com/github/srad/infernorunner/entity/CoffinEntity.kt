package com.github.srad.infernorunner.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.github.srad.infernorunner.core.CollisionMasks
import com.github.srad.infernorunner.core.ICollisionListener
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.base.*
import com.github.srad.infernorunner.entity.player.PlayerEntity
import com.github.srad.infernorunner.level.IMappable
import com.github.srad.infernorunner.level.MapInfo

class CoffinEntity : AbstractPhysicalEntity(Resource.coffin,
        PhysicalAttributes(
                BoxShape(.5f, 2.4f, 1f), Mass(10f),
                CollisionMasks(PlayerEntity::class, PhysicalBlockEntity::class, SpiderEntity::class)
        )),
        IMappable,
        ICollisionListener {
    override val name = "Coffin"
    override val mapInfo = MapInfo("Spawn", Color.BROWN)

    var reachedByPlayer = false
    private var state = AnimationRotation.Start
    private var rotation = 0f
    private val rotateBy = MathUtils.PI / 64

    private enum class AnimationRotation { Animate, Complete, Start }

    init {
        rigidBody.angularFactor = Vector3(0f, 0f, 0f)
    }

    override fun contactStarted(model: AbstractEntity) {
        if (!reachedByPlayer && state != AnimationRotation.Complete) {
            Resource.doorSound.load.play()
            state = AnimationRotation.Animate
            reachedByPlayer = true
        }
    }

    override fun update(delta: Float) {
        if (state == AnimationRotation.Animate) {
            rotation += rotateBy * delta
            if (rotation >= MathUtils.PI2) {
                state = AnimationRotation.Complete
            } else {
                rigidBody.worldTransform = rigidBody.worldTransform.rotateRad(Vector3.Y, rotateBy)
            }
        }
    }
}