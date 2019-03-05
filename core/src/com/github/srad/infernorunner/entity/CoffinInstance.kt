package com.github.srad.infernorunner.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.github.srad.infernorunner.core.CollisionMasks
import com.github.srad.infernorunner.core.ICollisionListener
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.player.PlayerInstance
import com.github.srad.infernorunner.level.IMappable
import com.github.srad.infernorunner.level.MapInfo

class CoffinInstance : PhysicalModelInstance(Resource.coffin,
        PhysicalAttributes(btBoxShape(Vector3(.5f, 2.4f, 1f)), 10f, CollisionMasks(PlayerInstance::class, PhysicalBlockInstance::class, SpiderInstance::class))),
        IMappable, ICollisionListener {
    override val name = "Coffin"
    override val mapInfo = MapInfo("Spawn", Color.BROWN)

    var reachedByPlayer = false
    private var state = AnimationRotation.Start
    private var rotation = 0f
    private val rotateBy = MathUtils.PI / 64

    private enum class AnimationRotation { Start, Animate, End }

    init {
        rigidBody.angularFactor = Vector3(0f, 0f, 0f)
    }

    override fun contactStarted(model: AbstractModelInstance) {
        if (!reachedByPlayer) {
            Resource.doorSound.load.play()
        }
    }

    override fun update(delta: Float) {
        if (state == AnimationRotation.End || !reachedByPlayer) {
            return
        }

        when (state) {
            AnimationRotation.Start -> state = AnimationRotation.Animate
            AnimationRotation.Animate -> {
                rotation += rotateBy
                if (rotation >= MathUtils.PI2) {
                    state = AnimationRotation.End
                } else {
                    rigidBody.worldTransform = rigidBody.worldTransform.rotateRad(Vector3.Y, rotateBy)
                }
            }
        }
    }
}