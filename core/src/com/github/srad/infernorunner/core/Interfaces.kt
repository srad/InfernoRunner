package com.github.srad.infernorunner.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.math.Vector3
import com.github.srad.infernorunner.entity.AbstractModelInstance
import com.github.srad.infernorunner.screen.AbstractStage
import kotlin.reflect.KClass

interface IGameLoopListener {
    fun create() {}
    fun handleInput(gameInfo: GameInfo, delta: Float) {}
    fun update(delta: Float) {}
    fun draw(window: Window, modelBatch: ModelBatch, spriteBatch: SpriteBatch) {}
}

interface ILoggable {
    fun logError(message: String) = logError("[error]", message)
    fun logError(tag: String, message: String) = Gdx.app.error(tag, message)

    fun logMessage(message: String) = logError("[message]", message)
    fun logMessage(tag: String, message: String) = Gdx.app.log(tag, message)

    fun logDebug(message: String) = logDebug("[debug]", message)
    fun logDebug(tag: String, message: String) = Gdx.app.debug(tag, message)
}

interface IScoreGiver {
    val score: Int
}

interface IScoreTaker {
    fun score(value: Int)
}

interface IDamageMaker {
    val damage: Int
}

interface IDamageTaker {
    fun damage(value: Int)
}

interface IDestroyable {
    val destroyedBy: Array<KClass<*>>
    // implementation optional, will be destructed and removed from visual + physical world.
    fun destruction() {}
}

interface IHealthGiver {
    val health: Int
}

interface IHealthTaker {
    fun health(value: Int)
}

interface ILevelFinisher {
    fun finish()
}

interface ILevelFinisherProvider

interface IModelSpawner {
    var spawnModel: Boolean
    fun spawn(): AbstractModelInstance
}

interface IStageProvider {
    var state: AbstractStage
}

interface ITeleporter {
    val destination: Vector3
    fun teleportStarted()
}

interface ITeleportable {
    var currentTeleporter: ITeleporter?
    fun teleport(teleporter: ITeleporter)
}