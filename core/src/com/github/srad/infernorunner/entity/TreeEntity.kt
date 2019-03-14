package com.github.srad.infernorunner.entity

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.base.AbstractEntity

class TreeEntity : AbstractEntity(Resource.treeModel) {
    override val name = "Tree"
    var rotate = false
    private val speed = MathUtils.random(0.1f, 2f) * MathUtils.randomSign()

    init {
        transform.scl(MathUtils.random(2f, 15f))
        transform.rotateRad(Vector3.Y, MathUtils.random(0f, MathUtils.PI2))
    }

    override fun update(delta: Float) {
        if (rotate) {
            transform.rotateRad(Vector3.Y, delta * speed)
        }
    }
}