package com.github.srad.infernorunner.entity

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.github.srad.infernorunner.core.IGameLoopListener
import com.github.srad.infernorunner.core.Resource
import java.util.function.Consumer
import java.util.function.Supplier
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

abstract class AModelInstance(modelResource: Resource<Model>) : ModelInstance(modelResource.load), IGameLoopListener {
    abstract val name: String

    private val animators = ArrayList<IAnimator>()

    protected open var animationTransform: Matrix4
        get() = transform
        set(value) {
            transform = value
        }

    open var translation: Vector3
        get() = transform.getTranslation(Vector3.Zero).cpy()
        set(value) {
            transform.setTranslation(value)
        }

    fun distanceTo(a: AModelInstance) = translation.sub(a.translation).len2()

    protected fun <T : IAnimator> addAnimation(klass: KClass<T>) {
        val animation = klass.primaryConstructor?.call(Supplier { animationTransform }, Consumer<Matrix4> { animationTransform = it })
        if (animation != null) {
            animators.add(animation)
        }
    }

    fun updateAnimations(delta: Float) {
        animators.forEach { it.update(delta) }
    }

    // update?
    var alive = true

    var markedAsRemoved = false

    // remove on next update?
    var remove = false

    open fun applyTransformation(transformation: Matrix4): Matrix4 = transform.set(transformation)
    open fun applyTranslation(v: Vector3): Matrix4 = transform.setTranslation(v)
    fun applyYRotation(rad: Float) = transform.rotateRad(Vector3.Y, rad)

    override fun toString() = "ModelInstance(name=$name,alive=$alive,remove=$remove)"
}