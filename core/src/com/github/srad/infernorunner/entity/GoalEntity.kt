package com.github.srad.infernorunner.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape
import com.badlogic.gdx.physics.bullet.collision.btSphereShape
import com.github.srad.infernorunner.core.CollisionMasks
import com.github.srad.infernorunner.core.ILevelFinisherProvider
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.animation.RotationAnimation
import com.github.srad.infernorunner.entity.animation.VerticalAnimator
import com.github.srad.infernorunner.entity.base.AbstractPhysicalEntity
import com.github.srad.infernorunner.entity.base.Mass
import com.github.srad.infernorunner.entity.base.PhysicalAttributes
import com.github.srad.infernorunner.entity.player.PlayerEntity
import com.github.srad.infernorunner.level.IMappable
import com.github.srad.infernorunner.level.MapInfo

class ShieldEntity : AbstractPhysicalEntity(Resource.shieldModel, PhysicalAttributes(btCylinderShape(Vector3(4f, 0.2f, 4f)), Mass(0f), CollisionMasks(PlayerEntity::class))), ILevelFinisherProvider {
    override val name = "Shield"

    override fun create() {
        super.create()
        addController(RotationAnimation::class)
    }
}

class GoalEntity : AbstractPhysicalEntity(Resource.goalModel, PhysicalAttributes(btSphereShape(2f), Mass(0f), CollisionMasks(PlayerEntity::class))), IMappable, ILevelFinisherProvider {
    override val name = "Shield"
    override val mapInfo = MapInfo("Goal", Color.MAGENTA)

    override fun create() {
        super.create()
        addController(VerticalAnimator::class)
        addController(RotationAnimation::class)
    }
}