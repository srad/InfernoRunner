package com.github.srad.infernorunner.screen.main

import com.badlogic.gdx.Gdx
import com.github.srad.infernorunner.InfernoRunner
import com.github.srad.infernorunner.core.GamePref
import com.github.srad.infernorunner.screen.AbstractScreen
import com.github.srad.infernorunner.screen.game.GameScreen
import com.github.srad.infernorunner.screen.main.stage.*

class MainMenu(private val game: InfernoRunner, private val settings: GamePref) : AbstractScreen() {
    private lateinit var mainMenuStage: MainMenuStage
    private lateinit var aboutStage: AboutStage
    private lateinit var storyStage: StoryStage
    private lateinit var settingsStage: SettingsStage
    private lateinit var statisticsStage: StatisticsStage

    override fun show() {
        super.show()

        statisticsStage = StatisticsStage(object : IStatisticsStageListener {
            override fun back() {
                showStage(mainMenuStage)
            }
        }, settings)

        aboutStage = AboutStage(object : IAboutListener {
            override fun back() {
                showStage(mainMenuStage)
            }
        })

        storyStage = StoryStage(object : IStoryStageListener {
            override fun back() {
                showStage(mainMenuStage)
            }
        })

        settingsStage = SettingsStage(object : ISettingsStageListener {
            override fun back() {
                showStage(mainMenuStage)
            }

            override fun changePlayerSettings(name: String) {

            }
        }, settings)

        mainMenuStage = MainMenuStage(object : IMainMenuListener {
            override fun play(level: Int) {
                mainMenuStage.hide()
                game.level = level
                game.showScreen(GameScreen::class)
            }

            override fun quit() {
                game.dispose()
                Gdx.app.exit()
            }

            override fun settings() {
                showStage(settingsStage)
            }

            override fun about() {
                showStage(aboutStage)
            }

            override fun statistics() {
                showStage(statisticsStage)
            }

            override fun story() {
                showStage(storyStage)
            }
        })
        currentStage = mainMenuStage
        currentStage.show()

        disposables.addAll(mainMenuStage, aboutStage, storyStage)
    }

    override fun pause() {
        super.pause()
        currentStage.hide()
    }

    override fun resume() {
        super.resume()
        currentStage.show()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        currentStage.viewport?.update(width, height, true)
    }
}