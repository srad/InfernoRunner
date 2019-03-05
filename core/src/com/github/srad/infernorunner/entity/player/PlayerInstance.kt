package com.github.srad.infernorunner.entity.player

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape
import com.badlogic.gdx.utils.Array
import com.github.srad.infernorunner.core.*
import com.github.srad.infernorunner.entity.*
import com.github.srad.infernorunner.entity.player.state.*
import com.github.srad.infernorunner.entity.state.StateManager
import com.github.srad.infernorunner.level.IMappable
import com.github.srad.infernorunner.level.MapInfo
import com.badlogic.gdx.Input.Keys as Key

class PlayerInstance(val listener: IPlayerListener) : PhysicalModelInstance(Resource.reaperModel, PhysicalAttributes(btCapsuleShape(1f, 2.3f), 1f, CollisionMasks(LifeInstance::class, PhysicalBlockInstance::class, ShieldInstance::class, PortalInstance::class, CoffinInstance::class, GravestoneInstance::class, ShopInstance::class, SpiderInstance::class, FountainInstance::class, GoalInstance::class))
), ICollisionListener, ILoggable, IMappable, IHealthTaker, IScoreTaker, IDamageTaker, ILevelFinisher, ITeleportable {
    override val name = "Player"
    override val mapInfo = MapInfo("Player", Color.GREEN)

    val stateManager = StateManager<PlayerInstance>()

    val maxLives = 3
    private var startPos = Vector3(0f, 12f, 0f)
    var score = 0
    var lives = maxLives

    private val bloods = Array<Blood>()

    val playerSettings = PlayerSettings()

    private var rigidBodyStartTransformation: Matrix4
    private var modelStartTransform: Matrix4
    private val modelStartRotation: Quaternion

    val cam: PerspectiveCamera = PerspectiveCamera(75f, com.github.srad.infernorunner.GameConfig.width.toFloat(), com.github.srad.infernorunner.GameConfig.height.toFloat())

    var closeToShop = false
    var respawn = false

    private val rotationDamage = (Math.PI / 2).toFloat()

    private val initCamDirection: Vector3
    private val initModelRotation: Quaternion

    private val movingState = MoveState(this)
    private val levelCompletedState = LevelCompletedState(this)
    private val voidState = VoidState(this)
    private val deadState = DeadState(this)
    private val groundState = GroundState(this)
    private val gameOverState = VoidState(this)
    private val flyState = FlyState(this)
    private val lookAroundState = LookAroundState(this)

    override var currentTeleporter: ITeleporter? = null

    init {
        initCamera()
        rigidBody.worldTransform = rigidBody.worldTransform.rotateRad(Vector3.Y, -MathUtils.PI / 2)
        //rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT

        // http://bulletphysics.org/mediawiki-1.5.8/index.php/Rigid_Bodies
        //rigidBody.setDamping(0.2f, 0f)
        //rigidBody.restitution = 10f
        // this is a bit-vector: allow = 1 or forbid = 0 movement along an axis: http://www.bulletphysics.org/mediawiki-1.5.8/index.php/Code_Snippets
        rigidBody.angularFactor = Vector3(0f, 0f, 0f)
        rigidBodyStartTransformation = rigidBody.worldTransform.cpy()
        modelStartTransform = transform.cpy()
        modelStartRotation = transform.getRotation(Quaternion()).cpy()

        initCamDirection = cam.direction.cpy()
        initModelRotation = transform.getRotation(Quaternion()).cpy()
    }

    private fun initCamera() {
        cam.position.set(startPos)
        cam.near = .1f
        cam.far = 400f
    }

    fun resetRotation() {
        cam.direction.set(initCamDirection)
        transform.rotate(initModelRotation)
    }

    fun incScore(value: Int) {
        score += value
    }

    fun incLive(value: Int) {
        if (lives < maxLives) {
            lives += value
        }
        if (lives == 0) {
            stateManager.state = PlayerState.GameOver
            listener.gameOver()
        }
    }

    override fun create() {
        super.create()
        stateManager.apply {
            addState(PlayerState.Ground, groundState, movingState, flyState, lookAroundState)
            addState(PlayerState.Air, movingState, flyState, lookAroundState)
            addState(PlayerState.LevelCompleted, levelCompletedState)
            addState(PlayerState.Dead, deadState)
            addState(PlayerState.Void, voidState)
            addState(PlayerState.GameOver, gameOverState)
            addState(PlayerState.LevelCompletedState, levelCompletedState)
            addState(PlayerState.StatusState, voidState)
            state = PlayerState.Ground
        }
        rigidBodyStartTransformation = rigidBody.worldTransform.cpy()
    }

    override fun handleInput(gameInfo: GameInfo, delta: Float) {
        stateManager.handleInput(gameInfo, delta)
    }

    override fun draw(window: Window, modelBatch: ModelBatch, spriteBatch: SpriteBatch) {
        if (bloods.count() > 0) {
            spriteBatch.begin()
            val it = bloods.iterator()
            while (it.hasNext()) {
                val b = it.next()
                spriteBatch.draw(b.texture, b.x, b.y, b.w, b.h)
            }
            spriteBatch.end()
        }
    }

    override fun update(delta: Float) {
        super.update(delta)
        stateManager.update(delta)

        // Random Bullet physics disabling counter-action.
        if (!rigidBody.isActive) {
            rigidBody.activate()
        }
    }

    fun applyDamage() {
        val size = MathUtils.random(200f, Gdx.graphics.width / 3f)
        bloods.add(Blood(Resource.bloodTexture.load, MathUtils.random(-50f, Gdx.graphics.width.toFloat()), MathUtils.random(-50f, Gdx.graphics.height.toFloat()), size, size))
        Resource.screamSound.load.play()
        cam.rotate(cam.direction, rotationDamage)
        listener.damage()
    }

    override fun health(value: Int) {
        incLive(value)
        removeBloodStain()
        listener.health()
    }

    override fun score(value: Int) {
        incScore(value)
        Resource.pickSound.load.play()
    }

    override fun damage(value: Int) {
        if (value > 0) {
            incScore(-value)
            applyDamage()
        }
    }

    override fun teleport(teleporter: ITeleporter) {
        bodyTranslation = teleporter.destination.add(0f, 10f, 0f)
    }

    override fun finish() {
        stateManager.state = PlayerState.LevelCompletedState
    }

    override fun contactStarted(model: AbstractModelInstance) {
        if (!closeToShop && (model is ShopInstance)) {
            closeToShop = true
        }

        val isOnGround = model is PhysicalBlockInstance || model is CoffinInstance || model is PortalInstance
        if (isOnGround) {
            stateManager.state = PlayerState.Ground
        }
    }

    fun removeBloodStain() {
        if (bloods.size > 0) {
            bloods.pop()
            cam.rotate(cam.direction, -rotationDamage)
            logDebug("Player", "Removed blood.")
        }
    }

    override fun contactEnded(model: AbstractModelInstance) {
        if (closeToShop && (model is ShopInstance)) {
            closeToShop = false
        }
    }

    fun reset() {
        lives = maxLives
        bloods.clear()
        resetAndRetainStats()
        while (bloods.size > 0) {
            removeBloodStain()
        }
    }

    fun resetAndRetainStats() {
        stateManager.state = PlayerState.Ground
    }
}