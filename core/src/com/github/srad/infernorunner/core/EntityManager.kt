package com.github.srad.infernorunner.core

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.physics.bullet.DebugDrawer
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Disposable
import com.github.srad.infernorunner.entity.AModelInstance
import com.github.srad.infernorunner.entity.player.IPlayerUpdateListener
import com.github.srad.infernorunner.entity.PhysicalModelInstance
import com.github.srad.infernorunner.entity.player.PlayerInstance

/** Would be one class with multiple inheritance in EntityManager but naahh. */
class MyContactListener(private val listener: ICollisionUserValueListener) : ContactListener() {
    override fun onContactStarted(userValue0: Int, userValue1: Int) = listener.contactStarted(userValue0, userValue1)
    override fun onContactEnded(userValue0: Int, userValue1: Int) = listener.contactEnded(userValue0, userValue1)
    override fun onContactProcessed(userValue0: Int, userValue1: Int) = listener.contactProcessed(userValue0, userValue1)
}

/**
 * Notice that the types of these interfaces are different.
 * One is for MyContactListener and the other for the #EntityManager.
 */
interface ICollisionListener {
    fun contactStarted(m1: AModelInstance, m2: AModelInstance)
    fun contactEnded(m1: AModelInstance, m2: AModelInstance)
    fun contactProcessed(m1: AModelInstance, m2: AModelInstance) {}
}

interface ICollisionUserValueListener {
    fun contactStarted(userValue0: Int, userValue1: Int)
    fun contactEnded(userValue0: Int, userValue1: Int)
    fun contactProcessed(userValue0: Int, userValue1: Int) {}
}

class EntityManager : Array<AModelInstance>(), Disposable, ICollisionUserValueListener, IGameLoopListener, ILoggable {
    val world: btDiscreteDynamicsWorld
    val debugDrawer: DebugDrawer
    private val collisionConfig: btDefaultCollisionConfiguration
    private val dispatcher: btCollisionDispatcher
    private val broadPhase: btDbvtBroadphase
    private val contactListener: MyContactListener
    private val constraintSolver: btSequentialImpulseConstraintSolver

    init {
        Bullet.init()
        collisionConfig = btDefaultCollisionConfiguration()
        dispatcher = btCollisionDispatcher(collisionConfig)
        broadPhase = btDbvtBroadphase()
        constraintSolver = btSequentialImpulseConstraintSolver()
        world = btDiscreteDynamicsWorld(dispatcher, broadPhase, constraintSolver, collisionConfig)
        world.gravity = Vector3(0f, -35f, 0f)
        contactListener = MyContactListener(this)
        debugDrawer = DebugDrawer()
        debugDrawer.debugMode = btIDebugDraw.DebugDrawModes.DBG_DrawWireframe
        world.debugDrawer = debugDrawer
    }

    override fun handleInput(gameInfo: GameInfo, delta: Float) {
        val it = iterator()
        while (it.hasNext()) {
            val e = it.next()
            e.handleInput(gameInfo, delta)
        }
    }

    fun update(delta: Float, playerInstance: PlayerInstance) {
        val it = iterator()
        while (it.hasNext()) {
            val e = it.next()
            if (e.markedAsRemoved) {
                continue
            }
            if (e.remove) {
                logDebug("EntityManager.remove", e.toString())
                if (e is PhysicalModelInstance) {
                    logDebug("EntityManager.update", "world.removeRigidBody($e)")
                    world.removeRigidBody(e.rigidBody)
                }
                e.markedAsRemoved = true
                e.alive = false
                // Don't remove until level end, otherwise the assigned array-indexes via userValue become wrong.
                // it.remove()
                // It's enough to clear the nodes so nothing gets rendered.
                e.nodes.clear()
                continue
            }
            if (e.alive) {
                e.update(delta)
                // TODO: EntityManager.add actually
                // set for each rigidbody: rigidBody.proceedToTransform(transform)
                // But this doesn't work for PlayerInstance (but for all other models).
                // Unclear why, since also other models are translated.
                if (e is PhysicalModelInstance) {
                    e.transformModel()
                }
                if (e is IPlayerUpdateListener) {
                    e.updatePlayer(delta, playerInstance)
                }
                if (e is IModelSpawner && e.spawnModel) {
                    e.spawnModel = false
                    add(e.spawn())
                }
                e.updateAnimations(delta)
            }
        }
    }

    override fun draw(window: Window, modelBatch: ModelBatch, spriteBatch: SpriteBatch) {
        val it = iterator()
        while (it.hasNext()) {
            val e = it.next()
            if (e.alive) {
                e.draw(window, modelBatch, spriteBatch)
            }
        }
    }

    override fun add(entity: AModelInstance) {
        if (entity is PhysicalModelInstance) {
            entity.index = size
            entity.rigidBody.userValue = size
            logDebug("EntityManager.add", entity.toString())
            world.addRigidBody(createRigidBody(entity), entity.maskInfo.mask.bits, entity.maskInfo.collisionMask.bits)
        }
        entity.create()
        super.add(entity)
    }

    private fun createRigidBody(entity: PhysicalModelInstance): btRigidBody {
        entity.rigidBody.collisionFlags = entity.rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK
        entity.rigidBody.proceedToTransform(entity.transform)
        entity.rigidBody.activate(true)

        return entity.rigidBody
    }

    override fun contactStarted(userValue0: Int, userValue1: Int) {
        val m1 = get(userValue0)
        val m2 = get(userValue1)
        logDebug("contactStarted", "$m1 -> $m2")
        if (m1 is ICollisionListener && m2.alive) {
            (m1 as ICollisionListener).contactStarted(m1, m2)
        }
    }

    override fun contactEnded(userValue0: Int, userValue1: Int) {
        val m1 = get(userValue0)
        val m2 = get(userValue1)
        if (m1 is PhysicalModelInstance && m2 is PhysicalModelInstance) {
            if (m1 is ICollisionListener) {
                (m1 as ICollisionListener).contactEnded(m1, m2)
            }
        }
    }

    override fun contactProcessed(userValue0: Int, userValue1: Int) {
        val m1 = get(userValue0)
        val m2 = get(userValue1)
        if (m1 is PhysicalModelInstance && m2 is PhysicalModelInstance) {
            if (m1 is ICollisionListener) {
                (m1 as ICollisionListener).contactProcessed(m1, m2)
            }
        }
    }

    fun unloadWorld() {
        logDebug("unloadWorld", "start")
        forEach { entity ->
            if (entity !is PlayerInstance) {
                try {
                    if (entity is PhysicalModelInstance) {
                        entity.rigidBody.activate(false)
                        entity.alive = false
                        world.removeRigidBody(entity.rigidBody)
                    }
                } catch (ex: Exception) {
                    logError("unloadWorld", ex.message.toString())
                }
            }
        }
        removeAll { it !is PlayerInstance }
        logDebug("unloadWorld", "complete")
    }

    override fun dispose() {
        val it = iterator()
        while (it.hasNext()) {
            val entity = it.next()

            if (entity is PhysicalModelInstance) {
                logDebug("EntityManager.dispose", "world.removeRigidBody($entity)")
                world.removeRigidBody(entity.rigidBody)
            }
            it.remove()
        }
        collisionConfig.dispose()
        dispatcher.dispose()
        broadPhase.dispose()
    }
}