package com.github.srad.infernorunner.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.github.srad.infernorunner.core.CollisionMasks
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.base.AbstractPhysicalEntity
import com.github.srad.infernorunner.entity.base.Mass
import com.github.srad.infernorunner.entity.base.PhysicalAttributes
import com.github.srad.infernorunner.entity.player.PlayerEntity
import com.github.srad.infernorunner.level.IMappable
import com.github.srad.infernorunner.level.MapInfo

class PhysicalBlockEntity : AbstractPhysicalEntity(Resource.block, PhysicalAttributes(btBoxShape(Vector3(1f, 1f, 1f)), Mass(0f), CollisionMasks(PlayerEntity::class, CoffinEntity::class, ShopEntity::class, FountainEntity::class, TowerEntity::class))), IMappable {
    override val name = "Physical-Block"
    override val mapInfo = MapInfo("Block", Color.ORANGE)

    private val direction: Float = MathUtils.randomSign().toFloat()
    private val speed: Float = MathUtils.random(1f, 30f)
    var rotate = false
    private var startY: Float? = null
    var animate = MathUtils.randomBoolean()
    var _animate = true //MathUtils.randomBoolean()
    private val animationDistance = MathUtils.random(.3f)
    private val animationUpdate = animationDistance / MathUtils.random(1.5f, 3f)

    enum class AnimationState { Rising, Falling }

    private var animation: AnimationState =  if (MathUtils.randomBoolean()) AnimationState.Falling else AnimationState.Rising

    override fun create() {
        super.create()
        if (MathUtils.randomBoolean()) {
            transform = transform.rotateRad(if (MathUtils.randomBoolean()) Vector3.X else Vector3.Z, MathUtils.PI / 2 * MathUtils.randomSign())
            rigidBody.worldTransform = transform
        }
    }

    override fun update(delta: Float) {
        syncModelWithPhysics = _animate //
        if (_animate) {
            if (startY == null) {
                startY = transform.getTranslation(Vector3.Zero).y
            }
            val maxDistanceReached = Math.abs(startY!! - transform.getTranslation(Vector3.Zero).y) >= animationDistance
            if (animation == AnimationState.Falling) {
                rigidBody.translate(Vector3(0f, -animationUpdate * delta, 0f))
                if (maxDistanceReached) {
                    animation = AnimationState.Rising
                }
            }
            if (animation == AnimationState.Rising) {
                if (startY!! - transform.getTranslation(Vector3.Zero).y <= 0) {
                    animation = AnimationState.Falling
                } else {
                    rigidBody.translate(Vector3(0f, animationUpdate * delta, 0f))
                }
            }

        }

        if (rotate) {
            rigidBody.worldTransform = rigidBody.worldTransform.rotate(Vector3.Y, direction * speed * delta)
        }
    }
}
