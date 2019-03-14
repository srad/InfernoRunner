package com.github.srad.infernorunner.entity

import com.badlogic.gdx.graphics.Color
import com.github.srad.infernorunner.core.CollisionMasks
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.base.AbstractPhysicalEntity
import com.github.srad.infernorunner.entity.base.CylinderShape
import com.github.srad.infernorunner.entity.base.Mass
import com.github.srad.infernorunner.entity.base.PhysicalAttributes
import com.github.srad.infernorunner.entity.player.PlayerEntity
import com.github.srad.infernorunner.level.IMappable
import com.github.srad.infernorunner.level.MapInfo

class ShopEntity : AbstractPhysicalEntity(Resource.shop, PhysicalAttributes(CylinderShape(3.5f, 3f, 3.5f), Mass(10f), CollisionMasks(PlayerEntity::class, PhysicalBlockEntity::class))),IMappable {
    override val name = "Shop"
    override val mapInfo = MapInfo("Shop", Color.RED)
}