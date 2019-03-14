package com.github.srad.infernorunner.entity

import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.base.AbstractEntity

class CrossEntity : AbstractEntity(Resource.cross) {
    override val name = "Cross"

    override fun update(delta: Float) {
        applyYRotation(delta)
    }
}