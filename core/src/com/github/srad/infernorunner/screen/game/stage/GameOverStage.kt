package com.github.srad.infernorunner.screen.game.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.screen.BloodyStage

interface IGameOverStageListener {
    fun mainmenu() {}
    fun retry() {}
    fun quit() {}
}

class GameOverStage(private val listener: IGameOverStageListener) : BloodyStage() {
    override val hasControls = true
    override val backgroundResource = Resource.satanEndTexture

    private val satanBreath: Sound by lazy { Resource.satanBreath.load }

    override fun build() {
        super.build()

        val btnRetry = createButton("Retry", object : ClickListener(Input.Buttons.LEFT) {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                listener.retry()
            }
        })


        val btnMainMenu = createButton("Main Menu", object : ClickListener(Input.Buttons.LEFT) {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                listener.mainmenu()
            }
        })

        val btnQuit = createButton("Quit", object : ClickListener(Input.Buttons.LEFT) {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                listener.quit()
            }
        })

        root.row().fillY()
        root.add(Label("GameOver", style)).center().colspan(3)
        root.row()
        root.center().bottom()
        root.add(btnRetry).center().bottom().padRight(10f)
        root.add(btnMainMenu).center().bottom().padRight(10f)
        root.add(btnQuit).center().bottom()
    }

    override fun hide() {
        super.hide()
        satanBreath.stop()
    }

    override fun show() {
        super.show()
        zoom = 1f
        Gdx.input.isCursorCatched = false
        satanBreath.play()
    }

    override fun update(delta: Float) {
        super.update(delta)
        val r = 30f
        zoom += (Math.sin(MathUtils.PI / 8 * delta.toDouble()) * r).toFloat()
    }
}