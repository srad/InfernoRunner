package com.github.srad.infernorunner.entity

import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.base.AbstractEntity

class GroundEntity : AbstractEntity(Resource.groundModel) {
    override val name = "Ground"
}