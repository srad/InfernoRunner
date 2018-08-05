package com.github.srad.infernorunner

import com.badlogic.gdx.Application
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.tools.texturepacker.TexturePacker
import com.github.srad.infernorunner.core.GamePref
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.screen.game.GameScreen
import com.github.srad.infernorunner.screen.main.MainMenu

class InfernoRunner : Game() {
    var level: Int = 1
    private val settings = GamePref()
    private val gameScreen: GameScreen by lazy { GameScreen(this, settings, level) }
    private val mainMenu: MainMenu by lazy { MainMenu(this, settings) }

    enum class Screen {
        MainMenu, Game
    }

    init {
        if (com.github.srad.infernorunner.GameConfig.rebuildAtlas) {
            val settings = TexturePacker.Settings()
            settings.maxWidth = 1024
            settings.maxHeight = 1024
            settings.debug = false
            TexturePacker.process(settings, "image/ui/button", "packed/ui", "buttons.atlas")
        }
    }

    override fun create() {
        Gdx.app.logLevel = if (com.github.srad.infernorunner.GameConfig.debug) Application.LOG_DEBUG else Application.LOG_ERROR
        showScreen(Screen.MainMenu)
    }

    override fun setScreen(screen: com.badlogic.gdx.Screen?) {
        throw RuntimeException("Use showScreen()")
    }

    fun showScreen(targetScreen: Screen) {
        if (this.screen != null) {
            this.screen.hide()
        }

        val s = when (targetScreen) {
            Screen.Game -> gameScreen
            Screen.MainMenu -> mainMenu
        }

        if (!s.hasShowed) {
            s.show()
        } else {
            s.resume()
        }

        this.screen = s
    }

    override fun dispose() {
        this.screen = null
        try {
            if (gameScreen.hasShowed) {
                gameScreen.dispose()
            }
            if (mainMenu.hasShowed) {
                mainMenu.dispose()
            }
            Resource.dispose()
        } catch (e: Exception) {
            println(e.message)
        }
    }
}