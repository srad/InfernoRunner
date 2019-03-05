package com.github.srad.infernorunner.entity

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState
import com.github.srad.infernorunner.core.CollisionMasks
import com.github.srad.infernorunner.core.Mask
import com.github.srad.infernorunner.core.MaskInfo
import com.github.srad.infernorunner.core.Resource
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

data class PhysicalAttributes(val shape: btCollisionShape, var mass: Float = 0f, val collidesWith: CollisionMasks)

abstract class PhysicalModelInstance(modelResource: Resource<Model>, val physics: PhysicalAttributes) : AbstractModelInstance(modelResource) {
    // TODO: MotionState doesnt update model
    //private val motionState: MotionState
    protected open var syncModelWithPhysics: Boolean = true

    companion object {
        private val entityMasks = ConcurrentHashMap<String, Mask>()
        fun createMask(klass: KClass<*>): Mask = entityMasks.getOrPut(klass.simpleName) { Mask.next() }
    }

    override var animationTransform: Matrix4
        get() = rigidBody.worldTransform
        set(value) {
            rigidBody.worldTransform = value
        }

    val maskInfo = MaskInfo(createMask(this::class), physics.collidesWith)

    open fun transformModel() {
        if (syncModelWithPhysics) {
            transform.set(rigidBody.worldTransform)
        }
    }

    var bodyTranslation: Vector3
        get() = rigidBody.worldTransform.getTranslation(Vector3.Zero)
        set(value) {
            rigidBody.worldTransform = rigidBody.worldTransform.setTranslation(value)
        }

    private var localInertia = Vector3.Zero
    private val constructorInfo: btRigidBody.btRigidBodyConstructionInfo
    val rigidBody: btRigidBody
    var index = -1

    init {
        if (physics.mass > 0f) {
            physics.shape.calculateLocalInertia(physics.mass, localInertia)
        } else {
            localInertia.set(0f, 0f, 0f)
        }
        //motionState = MotionState()
        constructorInfo = btRigidBody.btRigidBodyConstructionInfo(physics.mass, null, physics.shape, localInertia)
        rigidBody = btRigidBody(constructorInfo)
        //rigidBody.motionState = motionState
        //motionState.transform = this.transform
        //rigidBody.proceedToTransform(this.transform)
    }
}

class MotionState : btMotionState() {
    var transform = Matrix4()
    override fun getWorldTransform(worldTrans: Matrix4) {
        worldTrans.set(transform)
    }

    override fun setWorldTransform(worldTrans: Matrix4) {
        transform.set(worldTrans)
    }
}