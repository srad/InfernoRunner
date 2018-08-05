package com.github.srad.infernorunner.entity

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.github.srad.infernorunner.core.CollisionMasks
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.player.PlayerInstance

class GravestoneInstance : PhysicalModelInstance(Resource.gravestone, PhysicalAttributes(btBoxShape(Vector3(.5f, 2f, .5f)), 0f, CollisionMasks(PlayerInstance::class))) {
    override val name = "Gravestone"
}