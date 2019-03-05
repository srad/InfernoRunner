package com.github.srad.infernorunner.entity

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.utils.AnimationController
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.github.srad.infernorunner.core.IGameLoopListener
import com.github.srad.infernorunner.core.Resource
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer
import java.util.function.Supplier
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

abstract class AbstractModelInstance(modelResource: Resource<Model>) : ModelInstance(modelResource.load), IGameLoopListener {
    companion object {
        private val idCounter = AtomicInteger(0)
    }

    abstract val name: String

    /** If false skipped in game-loop. */
    var alive = true

    /** Has been pseudo-removed and is not rendered or updated anymore. */
    var removed = false

    /** Remove on next update. */
    var remove = false

    val id = idCounter.incrementAndGet()

    /** Could be refactored into an AnimationManager class, but blooooooat. */
    private val animators = ArrayList<AbstractAnimator>()
    private val controllers = Array<AnimationController>()

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

    /** Squared (not euclidean) distance to another model. */
    fun distanceTo(a: AbstractModelInstance) = translation.sub(a.translation).len2()

    protected fun <T : AbstractAnimator> addController(klass: KClass<T>) {
        val animation = klass.primaryConstructor?.call(Supplier { animationTransform }, Consumer<Matrix4> { animationTransform = it })
        if (animation != null) {
            animators.add(animation)
        }
    }

    /** Simplifies AnimationController API. Assumes that the ModelInstance has embedded, named animation controller information. */
    fun addController(
            name: String, speed: Float, offset: Float = 0f,
            onLoop: (animation: AnimationController.AnimationDesc?) -> Unit = {},
            onEnd: (animation: AnimationController.AnimationDesc?) -> Unit = {}) {
        val controller = AnimationController(this)

        controller.setAnimation(name, offset, -1f, -1, speed, object : AnimationController.AnimationListener {
            override fun onLoop(animation: AnimationController.AnimationDesc?) {
                onLoop(animation)
            }

            override fun onEnd(animation: AnimationController.AnimationDesc?) {
                onEnd(animation)
            }
        })

        controllers.add(controller)
    }

    override fun update(delta: Float) {
        animators.forEach { it.update(delta) }
        controllers.forEach { it.update(delta) }
    }

    open fun applyTransformation(transformation: Matrix4): Matrix4 = transform.set(transformation)
    open fun applyTranslation(v: Vector3): Matrix4 = transform.setTranslation(v)
    fun applyYRotation(rad: Float) = transform.rotateRad(Vector3.Y, rad)

    override fun toString() = "ModelInstance(id=$id,name=$name,alive=$alive,remove=$remove)"
}