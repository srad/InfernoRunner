package com.github.srad.infernorunner.entity

import com.badlogic.gdx.math.Vector3
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.base.AbstractEntity

class SphereEntity : AbstractEntity(Resource.sphere) {
    override val name = "Sphere"

    init {
        transform.setTranslation(Vector3.Zero)
        transform.scl(2f)
    }
}

