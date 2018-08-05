package com.github.srad.infernorunner.entity

import com.github.srad.infernorunner.core.Resource

class GroundInstance : AModelInstance(Resource.groundModel) {
    override val name = "Ground"
}