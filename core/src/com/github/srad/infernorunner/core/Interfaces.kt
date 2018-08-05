package com.github.srad.infernorunner.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.github.srad.infernorunner.entity.AModelInstance

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

    fun logDebug(message: String) = logError("[debug]", message)
    fun logDebug(tag: String, message: String) = Gdx.app.debug(tag, message)
}

interface IScoreGiver {
    val score: Int
}

interface IDestroyable {
    // implementation optional, will be destructed and removed from visual + physical world.
    fun destruction() {}
}

interface IHealthGiver {
    val health: Int
}

interface IModelSpawner {
    var spawnModel: Boolean
    fun spawn(): AModelInstance
}