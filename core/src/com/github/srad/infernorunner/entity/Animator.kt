package com.github.srad.infernorunner.entity

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import java.util.function.Consumer
import java.util.function.Supplier

abstract class IAnimator(val transformSupplier: Supplier<Matrix4>, val transformConsumer: Consumer<Matrix4>) {
    abstract fun update(delta: Float)
}

class VerticalAnimator(transformSupplier: Supplier<Matrix4>, transformConsumer: Consumer<Matrix4>) : IAnimator(transformSupplier, transformConsumer) {
    enum class AnimationState { Rising, Falling, Start }

    private var state = AnimationState.Start
    private var startY = transformSupplier.get().getTranslation(Vector3.Zero).y

    override fun update(delta: Float) {
        when (state) {
            AnimationState.Start -> {
                state = AnimationState.Rising
                startY = transformSupplier.get().getTranslation(Vector3.Zero).y
            }
            AnimationState.Rising -> {
                val pos = transformSupplier.get().getTranslation(Vector3.Zero).y
                if (pos < (startY + 2f)) {
                    transformConsumer.accept(transformSupplier.get().translate(Vector3(0f, delta * 1.5f, 0f)))
                }
                if (pos > (startY + 2f)) {
                    state = AnimationState.Falling
                }
            }
            AnimationState.Falling -> {
                val pos = transformSupplier.get().getTranslation(Vector3.Zero).y
                transformConsumer.accept(transformSupplier.get().translate(Vector3(0f, -delta * 1.5f, 0f)))
                if (pos < startY) {
                    state = AnimationState.Rising
                }
            }
        }
    }
}

class RotationAnimation(transformSupplier: Supplier<Matrix4>, transformConsumer: Consumer<Matrix4>) : IAnimator(transformSupplier, transformConsumer) {
    override fun update(delta: Float) {
        transformConsumer.accept(transformSupplier.get().rotateRad(Vector3.Y, 3f * delta))
    }
}