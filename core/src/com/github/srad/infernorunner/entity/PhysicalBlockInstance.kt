package com.github.srad.infernorunner.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.github.srad.infernorunner.core.CollisionMasks
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.player.PlayerInstance
import com.github.srad.infernorunner.level.IMappable
import com.github.srad.infernorunner.level.MapInfo

class PhysicalBlockInstance : PhysicalModelInstance(Resource.block, PhysicalAttributes(btBoxShape(Vector3(1f, 1f, 1f)), 0f, CollisionMasks(PlayerInstance::class, CoffinInstance::class, ShopInstance::class, FountainInstance::class, TowerInstance::class))), IMappable {
    override val name = "Physical-Block"
    override val mapInfo = MapInfo("Block", Color.ORANGE)

    private val direction: Float = MathUtils.randomSign().toFloat()
    private val speed: Float = MathUtils.random(1f, 30f)
    var rotate = false
    var startY: Float? = null
    var animate = false

    enum class AnimationState { Rising, Falling }

    private var animation: AnimationState = AnimationState.Rising

    override fun create() {
        super.create()
        if (MathUtils.randomBoolean()) {
            transform = transform.rotateRad(if (MathUtils.randomBoolean()) Vector3.X else Vector3.Z, MathUtils.PI / 2 * MathUtils.randomSign())
            rigidBody.worldTransform = transform
        }
    }

    override fun update(delta: Float) {
        syncModelWithPhysics = animate //
        if (animate) {
            if (startY == null) {
                startY = transform.getTranslation(Vector3.Zero).y
            }
            val maxDistanceReached = Math.abs(startY!! - transform.getTranslation(Vector3.Zero).y) >= 10f
            if (animation == AnimationState.Falling) {
                rigidBody.translate(Vector3(0f, -0.05f, 0f))
                if (maxDistanceReached) {
                    animation = AnimationState.Rising
                }
            }
            if (animation == AnimationState.Rising) {
                if (startY!! - transform.getTranslation(Vector3.Zero).y <= 0) {
                    animation = AnimationState.Falling
                } else {
                    rigidBody.translate(Vector3(0f, 0.05f, 0f))
                }
            }

        }

        if (rotate) {
            rigidBody.worldTransform = rigidBody.worldTransform.rotate(Vector3.Y, direction * speed * delta)
        }
    }
}
