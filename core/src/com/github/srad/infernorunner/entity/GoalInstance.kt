package com.github.srad.infernorunner.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape
import com.badlogic.gdx.physics.bullet.collision.btSphereShape
import com.github.srad.infernorunner.core.CollisionMasks
import com.github.srad.infernorunner.core.ILevelFinisherProvider
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.player.PlayerInstance
import com.github.srad.infernorunner.level.IMappable
import com.github.srad.infernorunner.level.MapInfo

class ShieldInstance : PhysicalModelInstance(Resource.shieldModel, PhysicalAttributes(btCylinderShape(Vector3(4f, 0.2f, 4f)), 0f, CollisionMasks(PlayerInstance::class))), ILevelFinisherProvider {
    override val name = "Shield"

    override fun create() {
        super.create()
        addController(RotationAnimation::class)
    }
}

class GoalInstance : PhysicalModelInstance(Resource.goalModel, PhysicalAttributes(btSphereShape(2f), 0f, CollisionMasks(PlayerInstance::class))), IMappable, ILevelFinisherProvider {
    override val name = "Shield"
    override val mapInfo = MapInfo("Goal", Color.MAGENTA)

    override fun create() {
        super.create()
        addController(VerticalAnimator::class)
        addController(RotationAnimation::class)
    }
}