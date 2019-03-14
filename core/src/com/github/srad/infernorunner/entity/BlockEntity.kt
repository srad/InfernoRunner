package com.github.srad.infernorunner.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.base.AbstractEntity
import com.github.srad.infernorunner.level.IMappable
import com.github.srad.infernorunner.level.MapInfo

class BlockEntity : AbstractEntity(Resource.block2), IMappable {
    override val name = "Block-Transparent"
    override val mapInfo = MapInfo("Block", Color.ORANGE)

    override fun create() {
        super.create()
        transform = transform.rotateRad(Vector3.X, MathUtils.PI)
    }
}