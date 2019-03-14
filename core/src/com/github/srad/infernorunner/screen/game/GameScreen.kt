package com.github.srad.infernorunner.screen.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.utils.Array
import com.github.srad.infernorunner.InfernoRunner
import com.github.srad.infernorunner.core.*
import com.github.srad.infernorunner.entity.CoffinEntity
import com.github.srad.infernorunner.entity.player.IPlayerListener
import com.github.srad.infernorunner.entity.player.PlayerEntity
import com.github.srad.infernorunner.entity.player.behavior.PlayerBehavior
import com.github.srad.infernorunner.level.AbstractLevelCreator
import com.github.srad.infernorunner.level.LevelReader
import com.github.srad.infernorunner.screen.AbstractScreen
import com.github.srad.infernorunner.screen.IStageListener
import com.github.srad.infernorunner.screen.game.stage.*
import com.github.srad.infernorunner.screen.main.MainMenu
import com.badlogic.gdx.Input.Keys as Key

class DebugModels : Array<ModelInstance>()

class LevelInfo(var currentLevel: Int, var level: AbstractLevelCreator? = null)

class GameScreen(private val game: InfernoRunner, private val settings: GamePref) : AbstractScreen(), ILoggable {
    val gameStatistics = GameStatistics()
    val levelInfo = LevelInfo(game.level)

    private val entityManager = EntityManager()
    private val environment = Environment()

    private var drawModelWithDebug = true

    private val hellMusic: Music by lazy { Resource.hellMusic.load }

    private val playerEntity = PlayerEntity(object : IPlayerListener {
        override fun death() {
            gameStatistics.deaths.incrementAndGet()
        }

        override fun gameOver() {
            gameStatistics.gameOvers.incrementAndGet()
        }

        override fun jump() {
            gameStatistics.jumps.incrementAndGet()
        }

        override fun damage() {
            gameStatistics.hits.incrementAndGet()
        }

        override fun health() {
            gameStatistics.lives.incrementAndGet()
        }
    })

    private val debugModels = DebugModels()

    private val hudStage: HudStage by lazy { HudStage(levelInfo, playerEntity, levelInfo.level!!, settings) }

    private val shopStage: ShopStage by lazy {
        ShopStage(playerEntity, object : IShopListener {
            override fun open() {
                inputManager.clearInput()
            }

            override fun close() {
                shopStage.hide()
                showStage(hudStage)
                catchCursor()
                listenInput()
            }

            override fun purchase() {
                gameStatistics.purchases.incrementAndGet()
            }
        })
    }

    private val gameOverStage: GameOverStage by lazy {
        GameOverStage(object : IGameOverStageListener {
            override fun retry() {
                showStage(hudStage)
                respawn()
            }

            override fun mainmenu() {
                game.showScreen(MainMenu::class)
            }

            override fun quit() {
                exit()
            }
        })
    }

    private val winStage: WinStage by lazy {
        WinStage(object : IWinStageListener {
            override fun confirm() {
                levelInfo.currentLevel = 0
                loadNextLevel()
                respawn()
                game.showScreen(MainMenu::class)
            }

            override fun quit() {
                game.dispose()
                exit()
            }
        })
    }

    private val levelFinishedStage: LevelFinishedStage by lazy {
        LevelFinishedStage(playerEntity, settings, levelInfo.level!!, object : ILevelFinishedStage {
            override fun ok() {
                loadNextLevel()
                showStage(hudStage)
            }
        })
    }

    private val infoStage: InfoStage by lazy {
        InfoStage(levelInfo.level!!, settings, object : IStageListener {
            override fun show() {
                saveStats()
            }
        })
    }

    override fun show() {
        super.show()
        levelInfo.currentLevel = game.level
        hellMusic.isLooping = true
        hellMusic.play()

        entityManager.add(playerEntity)

        catchCursor()
        Gdx.input.setCursorPosition(Gdx.graphics.width / 2, Gdx.graphics.height / 2)
        loadNextLevel()

        hudStage.show()
        currentStage = hudStage
        disposables.addAll(hudStage, shopStage, gameOverStage, winStage, infoStage, levelFinishedStage)
        listenInput()
    }

    private fun loadNextLevel() {
        stopped = true
        val file = "level/${levelInfo.currentLevel}.json"
        if (Gdx.files.internal(file).exists()) {
            hellMusic.stop()
            entityManager.unloadWorld()
            environment.clear()
            levelInfo.level = LevelReader(file, entityManager, environment, "${levelInfo.currentLevel}")
            levelInfo.level!!.build(playerEntity)
            playerEntity.resetAndRetainStats()
            playerEntity.resetRotation()
            hellMusic.play()
            levelInfo.currentLevel += 1
        } else {
            // No more levels, won...
            playerEntity.behaviorManager.behavior = PlayerBehavior.Won
        }
        stopped = false
    }

    override fun update(delta: Float) {
        super.update(delta)

        // TODO: Move to entity with IStageProvider implementation. Any entity should be allowed to show stages.
        when (playerEntity.behaviorManager.behavior) {
            PlayerBehavior.StatusState -> showStage(infoStage)
            PlayerBehavior.Won -> showStage(winStage)
            PlayerBehavior.GameOver -> showStage(gameOverStage)
            PlayerBehavior.LevelCompletedState -> showStage(levelFinishedStage)
            else -> showStage(hudStage)
        }

        // TODO: Implement IStageProvider
        shopStage.update(delta)

        entityManager.update(delta, playerEntity)
    }

    /** Spawn at closest reached spawn point. "Reaching" happens by touching the spawn. */
    private fun respawn() {
        val closestSpawn = entityManager
                .filter { it is CoffinEntity && it.reachedByPlayer }
                .map { Pair(it, playerEntity.translation.sub(it.translation)) }
                .sortedBy { it.second.len2() }
                .first()

        playerEntity.bodyTranslation = closestSpawn.first.translation.add(0f, 6f, 0f)

        if (playerEntity.behaviorManager.any(PlayerBehavior.GameOver, PlayerBehavior.Won)) {
            playerEntity.reset()
            catchCursor()
        }
    }

    override fun drawDebug(window: Window, modelBatch: ModelBatch, spriteBatch: SpriteBatch) {
        entityManager.debugDrawer.begin(playerEntity.cam)
        entityManager.world.debugDrawWorld()
        entityManager.debugDrawer.end()
        hudStage.debugDraw()
    }

    override fun draw(window: Window, modelBatch: ModelBatch, spriteBatch: SpriteBatch) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        if (!enableDebugging || drawModelWithDebug) {
            modelBatch.begin(playerEntity.cam)
            modelBatch.render(entityManager, environment)
            modelBatch.render(debugModels, environment)
            modelBatch.end()

            entityManager.draw(window, modelBatch, spriteBatch)
        }
        super.draw(window, modelBatch, spriteBatch)
        shopStage.draw()
    }

    override fun handleInput(gameInfo: GameInfo, delta: Float) {
        entityManager.handleInput(gameInfo, delta)

        val statusInput = gameInfo.key.pressed(Key.TAB) || gameInfo.controller.select;
        val inStatusState = playerEntity.behaviorManager.behavior == PlayerBehavior.StatusState

        if (inStatusState && !statusInput) {
            playerEntity.behaviorManager.popBehavior()
            return
        }

        if (!inStatusState && statusInput) {
            playerEntity.behaviorManager.pushBehavior = PlayerBehavior.StatusState
            return
        }

        when {
            gameInfo.key.pressed(Key.F1) || gameInfo.controller.x -> {
                enableDebugging = true
                hellMusic.stop()
            }
            gameInfo.key.pressed(Key.F2) || gameInfo.controller.y -> {
                if (enableDebugging) {
                    enableDebugging = false
                    hellMusic.play()
                }
            }
            gameInfo.key.pressed(Key.ESCAPE) || gameInfo.controller.start -> {
                saveStats()
                game.showScreen(MainMenu::class)
            }
            gameInfo.key.pressed(Key.F3) -> isPaused = true
            gameInfo.key.pressed(Key.F4) -> isPaused = false
            gameInfo.key.pressed(Key.F5) -> {
                drawModelWithDebug = true
                enableDebugging = true
            }
            gameInfo.key.pressed(Key.F6) -> drawModelWithDebug = false
        }
    }

    fun saveStats() {
        try {
            synchronized(this) {
                for (type in PrefType.statistics) {
                    settings.set(type, settings.get(type) + when (type) {
                        PrefType.PlayerGameOvers -> gameStatistics.gameOvers.get()
                        PrefType.PlayerDeaths -> gameStatistics.deaths.get()
                        PrefType.PlayerJumps -> gameStatistics.jumps.get()
                        PrefType.PlayerHits -> gameStatistics.hits.get()
                        PrefType.PlayerLives -> gameStatistics.lives.get()
                        PrefType.PlayerPurchases -> gameStatistics.purchases.get()
                        else -> error("Unknown statistics to save: ${type.typeName}")
                    })
                }
                gameStatistics.reset()
            }
        } catch (e: Exception) {
            e.message?.let { logError("saveStats", it) }
        }
    }

    override fun pause() {
        super.pause()
        hellMusic.pause()
    }

    override fun resume() {
        super.resume()
        if (playerEntity.behaviorManager.any(PlayerBehavior.GameOver, PlayerBehavior.Won)) {
            respawn()
        }
        showStage(hudStage)
        hudStage.show()
        currentStage = hudStage
        hellMusic.play()
    }

    override fun hide() {
        super.hide()
        hellMusic.stop()
    }

    override fun dispose() {
        hellMusic.stop()
        entityManager.dispose()
        super.dispose()
    }
}