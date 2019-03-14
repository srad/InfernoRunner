package com.github.srad.infernorunner

import com.badlogic.gdx.Application
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.tools.texturepacker.TexturePacker
import com.badlogic.gdx.utils.Array
import com.github.srad.infernorunner.core.GamePref
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.screen.AbstractScreen
import com.github.srad.infernorunner.screen.game.GameScreen
import com.github.srad.infernorunner.screen.main.MainMenu
import kotlin.reflect.KClass

class InfernoRunner : Game() {
    private val settings = GamePref()

    private val screenTypes = arrayOf(GameScreen::class, MainMenu::class)
    private val screens = Array<AbstractScreen>()
    private val startScreen = GameScreen::class

    var level = 1

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
        showScreen(startScreen)
    }

    override fun setScreen(screen: com.badlogic.gdx.Screen?) {
        throw RuntimeException("Use showScreen()")
    }

    fun <T : AbstractScreen>showScreen(targetScreen: KClass<T>) {
        if (this.screen != null) {
            this.screen.hide()
        }

        val isScreenConstructed = screens.firstOrNull { it::class == targetScreen }
        var s: AbstractScreen

        if (isScreenConstructed == null) {
            val screenType = screenTypes.first { it == targetScreen }
            s = screenType.constructors.first().call(this, settings)
            screens.add(s)
        } else {
            s = screens.first { it::class == targetScreen }
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
            screens.forEach { if (it.hasShowed) it.dispose() }
            Resource.dispose()
        } catch (e: Exception) {
            println(e.message)
        }
    }
}