package com.github.srad.infernorunner.entity

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.github.srad.infernorunner.core.CollisionMasks
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.base.AbstractPhysicalEntity
import com.github.srad.infernorunner.entity.base.Mass
import com.github.srad.infernorunner.entity.base.PhysicalAttributes
import com.github.srad.infernorunner.entity.player.PlayerEntity

class FountainEntity : AbstractPhysicalEntity(Resource.fountainModel, PhysicalAttributes(btBoxShape(Vector3(2.5f, 0.8f, 2.5f)), Mass(1f), CollisionMasks(PlayerEntity::class, PhysicalBlockEntity::class))) {
    override val name = "Fountain"

    override fun transformModel() {
        transform.setTranslation(rigidBody.worldTransform.getTranslation(Vector3.Zero))
    }

    init {
        transform.scl(0.03f)
        addController("Fountain.001|Fountain.001Action", 0.1f)
        addController("Fountain.002|Fountain.001Action", 0.1f)
        addController("Fountain.003|Fountain.001Action", 0.1f)
        addController("Fountain.004|Fountain.001Action", 0.1f)
        addController("Fountain.005|Fountain.001Action", 0.1f)
    }
}