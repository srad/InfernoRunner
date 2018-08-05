package com.github.srad.infernorunner.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape
import com.github.srad.infernorunner.core.CollisionMasks
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.player.PlayerInstance
import com.github.srad.infernorunner.level.IMappable
import com.github.srad.infernorunner.level.MapInfo

class ShopInstance : PhysicalModelInstance(Resource.shop, PhysicalAttributes(btCylinderShape(Vector3(3.5f, 3f, 3.5f)), 10f, CollisionMasks(PlayerInstance::class, PhysicalBlockInstance::class))), IMappable {
    override val name = "Shop"
    override val mapInfo = MapInfo("Shop", Color.RED)
}