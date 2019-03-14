package com.github.srad.infernorunner.entity

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.github.srad.infernorunner.core.CollisionMasks
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.base.AbstractPhysicalEntity
import com.github.srad.infernorunner.entity.base.Mass
import com.github.srad.infernorunner.entity.base.PhysicalAttributes
import com.github.srad.infernorunner.entity.player.PlayerEntity

class GravestoneEntity : AbstractPhysicalEntity(Resource.gravestone, PhysicalAttributes(btBoxShape(Vector3(.5f, 2f, .5f)), Mass(0f), CollisionMasks(PlayerEntity::class))) {
    override val name = "Gravestone"
}