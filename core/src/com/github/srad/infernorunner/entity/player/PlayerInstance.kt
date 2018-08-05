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
import com.badlogic.gdx.utils.TimeUtils
import com.github.srad.infernorunner.core.*
import com.github.srad.infernorunner.entity.*
import com.github.srad.infernorunner.entity.state.StateManager
import com.github.srad.infernorunner.level.IMappable
import com.github.srad.infernorunner.level.MapInfo
import com.badlogic.gdx.Input.Keys as Key

class PlayerInstance(val listener: IPlayerListener) : PhysicalModelInstance(Resource.reaperModel, PhysicalAttributes(btCapsuleShape(1f, 2.3f), 1f, CollisionMasks(LifeInstance::class, PhysicalBlockInstance::class, ShieldInstance::class, PortalInstance::class, CoffinInstance::class, GravestoneInstance::class, ShopInstance::class, FountainInstance::class, GoalInstance::class))
), ICollisionListener, ILoggable, IMappable {
    override val name = "Player"
    override val mapInfo = MapInfo("Player", Color.GREEN)

    val stateManager = StateManager<PlayerInstance>()

    val maxLives = 3
    private var startPos = Vector3(0f, 12f, 0f)
    var score = 0
    var lives = maxLives
    var isDead = false
    var readForNextLevel = false
    var currentPortal: PortalInstance? = null

    private var deathTime = 0L
    private val bloods = Array<Blood>()

    val playerSettings = PlayerSettings()

    private var rigidBodyStartTransformation: Matrix4
    private var modelStartTransform: Matrix4
    private val modelStartRotation: Quaternion

    val cam: PerspectiveCamera = PerspectiveCamera(75f, com.github.srad.infernorunner.GameConfig.width.toFloat(), com.github.srad.infernorunner.GameConfig.height.toFloat())

    var isLevelCompleted = false
    var closeToShop = false
    var gameOver = false
    private var levelCompleteTime = 0L
    var respawn = false

    private val rotationAfterBlood = (Math.PI / 2).toFloat()

    var hasWon = false

    private val initCamDirection: Vector3
    private val initModelRotation: Quaternion

    private val movingState = MoveState(this)
    private val levelCompletedState = LevelCompletedState(this)
    private val readForNextLevelState = ReadForNextLevelState(this)
    private val groundState = GroundState(this)
    private val gameOverState = VoidState(this)
    private val flyState = FlyState(this)
    private val lookAroundState = LookAroundState(this)

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
    }

    fun decScore(value: Int) {
        if (score > 0) {
            score -= value
        }
    }

    override fun create() {
        super.create()
        stateManager.apply {
            addState(PlayerState.Ground, groundState, movingState, flyState, lookAroundState)
            addState(PlayerState.Air, movingState, flyState, lookAroundState)
            addState(PlayerState.LevelCompleted, levelCompletedState)
            addState(PlayerState.Dead, readForNextLevelState)
            addState(PlayerState.GameOver, gameOverState)
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
        stateManager.update(delta)
        if (isLevelCompleted && TimeUtils.timeSinceMillis(levelCompleteTime) > 7) {
            readForNextLevel = true
            return
        }

        if (isDead) {
            // Wait for death animation / screen
            if (TimeUtils.timeSinceMillis(deathTime) < 2000) {
                return
            } else {
                isDead = false
                deathTime = 0
                respawn = true
                gameOver = false
                Resource.screamSound.load.stop()
            }
            return
        }

        // TODO: random physics disabling. Probably related to the flags or just bug.
        if (!rigidBody.isActive) {
            rigidBody.activate()
        }

        respawn = false

        cam.position.set(rigidBody.worldTransform.getTranslation(Vector3.Zero).cpy().add(0f, 2f, 0f))
        cam.update()

        // Death
        if (rigidBody.worldTransform.getTranslation(Vector3.Zero).y < -30f) {
            addBloodStain()
            deathTime = TimeUtils.millis()
            isDead = true
            if (lives > 0) {
                lives -= 1
            }
            gameOver = lives == 0
            listener.death()
            if (gameOver) {
                listener.gameOver()
            }
        }
    }

    fun addBloodStain() {
        val size = MathUtils.random(200f, Gdx.graphics.width / 3f)
        bloods.add(Blood(Resource.bloodTexture.load, MathUtils.random(-50f, Gdx.graphics.width.toFloat()), MathUtils.random(-50f, Gdx.graphics.height.toFloat()), size, size))
        Resource.screamSound.load.play()
        cam.rotate(cam.direction, rotationAfterBlood)
        listener.damage()
    }

    override fun contactStarted(m1: AModelInstance, m2: AModelInstance) {
        // Can be refactored into #EntityManager to involve specific callback, but that good enough now.
        // Keep chained, since m2 can be multiple things.
        if (m2 is IScoreGiver) {
            incScore(m2.score)
            m2.alive = false
            //m2.remove = true
            Resource.pickSound.load.play()
        }
        if (m2 is IHealthGiver) {
            incLive(m2.health)
            m2.alive = false
            //m2.remove = true
            removeBloodStain()
            listener.health()
        }
        if (m2 is IDestroyable) {
            m2.destruction()
            m2.alive = false
            //m2.remove = true
        }
        if (m2 is PortalInstance) {
            currentPortal = m2
        }
        if (m2 is CoffinInstance) {
            // spawn-point reached
            //if (!m2.reachedByPlayer) {
            Resource.doorSound.load.play()
            //}
            m2.reachedByPlayer = true
        }

        if (!isLevelCompleted && (m2 is GoalInstance)) {
            isLevelCompleted = true
            levelCompleteTime = TimeUtils.millis()
            Resource.comeHere.load.play()
        }
        if (!closeToShop && (m2 is ShopInstance)) {
            closeToShop = true
        }
        val isOnGround = m2 is PhysicalBlockInstance || m2 is CoffinInstance || m2 is PortalInstance
        if (isOnGround) {
            stateManager.state = PlayerState.Ground
        }
    }

    fun removeBloodStain() {
        if (bloods.size > 0) {
            bloods.pop()
            cam.rotate(cam.direction, -rotationAfterBlood)
            logDebug("Player", "Removed blood.")
        }
    }

    override fun contactEnded(m1: AModelInstance, m2: AModelInstance) {
        if (closeToShop && (m2 is ShopInstance)) {
            closeToShop = false
        }
        if (m2 is PortalInstance) {
            currentPortal = null
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
        hasWon = false
        gameOver = false
        isDead = false
        isLevelCompleted = false
        readForNextLevel = false
    }
}