package com.github.srad.infernorunner.entity

import com.github.srad.infernorunner.core.Resource

class CrossInstance : AbstractModelInstance(Resource.cross) {
    override val name = "Cross"

    override fun update(delta: Float) {
        applyYRotation(delta)
    }
}