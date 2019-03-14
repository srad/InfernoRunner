package com.github.srad.infernorunner.entity.base

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Disposable
import com.github.srad.infernorunner.core.*

/** Foundational structure for a game-loop with useful functionality. */
abstract class AbstractGameLoop : ApplicationListener, IGameLoopListener, ILoggable {
    var enableDebugging = false

    companion object {
        private val config: DefaultShader.Config
            get() {
                val config = DefaultShader.Config()
                config.numDirectionalLights = 1
                config.numPointLights = 15
                return config
            }
        val modelBatch = ModelBatch(DefaultShaderProvider(config))
        val spriteBatch = SpriteBatch()
    }

    protected open val disposables = Array<Disposable>()
    protected val inputManager = InputManager()

    var isPaused = false
    var hasShowed = false
    var stopped = false

    protected open fun drawDebug(window: Window, modelBatch: ModelBatch, spriteBatch: SpriteBatch) {}

    override fun create() {
        inputManager.gameInfo.window.width = Gdx.graphics.width.toFloat()
        inputManager.gameInfo.window.height = Gdx.graphics.height.toFloat()

        inputManager.gameInfo.window.midX = inputManager.gameInfo.window.width / 2
        inputManager.gameInfo.window.midY = inputManager.gameInfo.window.height / 2

        Controllers.addListener(inputManager)
    }

    protected open fun onStopped() {}

    private fun render(delta: Float) {
        handleInput(inputManager.gameInfo, delta)
        if (!isPaused && !stopped) {
            update(delta)
        }
        if (!isPaused && !stopped) {
            draw(inputManager.gameInfo.window, modelBatch, spriteBatch)
        }
        if (stopped) {
            onStopped()
        }
        if (enableDebugging && !stopped) {
            drawDebug(inputManager.gameInfo.window, modelBatch, spriteBatch)
        }
    }

    override fun render() {
        render(Gdx.graphics.deltaTime)
    }

    override fun pause() {
        isPaused = true
    }

    override fun resume() {
        isPaused = false
        Gdx.input.inputProcessor = inputManager
    }

    override fun resize(width: Int, height: Int) {
        inputManager.gameInfo.window.width = width.toFloat()
        inputManager.gameInfo.window.height = height.toFloat()
    }

    override fun dispose() {
        disposables.add(modelBatch)
        disposables.add(spriteBatch)
        // Don't forget that assets is a singleton, don't call
        // assets.dispose(), otherwise other entities get null-pointer references...
        disposables.forEach { d ->
            try {
                d?.dispose()
            } catch (ex: Exception) {
                logError("disposing", ex.message.toString())
            }
        }
    }

    fun exit() {
        dispose()
        Gdx.app.exit()
    }
}