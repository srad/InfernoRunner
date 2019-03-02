package com.github.srad.infernorunner.entity

import com.badlogic.gdx.graphics.g3d.utils.AnimationController
import com.badlogic.gdx.utils.Array

class AnimationControllers : Array<AnimationController>() {
    fun addAnimation(name: String, model: AbstractModelInstance, speed: Float, offset: Float = 0f) {
        val controller = AnimationController(model)
        controller.setAnimation(name, offset, -1f, -1, speed, object : AnimationController.AnimationListener {
            override fun onLoop(animation: AnimationController.AnimationDesc?) {
            }

            override fun onEnd(animation: AnimationController.AnimationDesc?) {
            }
        })
        add(controller)
    }

    fun update(delta: Float) {
        for (c in this) {
            c.update(delta)
        }
    }
}