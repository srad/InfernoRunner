package com.github.srad.infernorunner.screen.game.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.screen.BloodyStage

interface IWinStageListener {
    fun confirm() {}
    fun quit() {}
}

class WinStage(private val listener: IWinStageListener) : BloodyStage() {
    override val hasControls = true
    override val backgroundResource = Resource.satanFace
    private val satanBreath: Sound by lazy { Resource.satanBreath.load }

    private lateinit var vertexShader: String
    private lateinit var fragmentShader: String

    private var dTDraw = 0f
    private var maxFlickerTime = 0f
    private var drawFlicker = false
    private var zoomIn: Long = 0L

    override fun build() {
        super.build()

        vertexShader = Gdx.files.internal("shader/flicker_with_mouse/vertex.glsl").readString()
        fragmentShader = Gdx.files.internal("shader/flicker_with_mouse/fragment.glsl").readString()
        shader = ShaderProgram(vertexShader, fragmentShader)

        val btnStart = createButton("Quit", object : ClickListener(Input.Buttons.LEFT) {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                satanBreath.stop()
                listener.quit()
            }
        })

        root.add(Label("Won! Welcome to HELLLLLLLLLLLLLLLLLLLL", style)).center().bottom()
        root.row()

        root.center().bottom()
        root.row().fillY()
        root.add(btnStart).center().bottom()
    }

    override fun update(delta: Float) {
        if (drawFlicker) {
            dTDraw -= 1
            if (dTDraw <= 0) {
                drawFlicker = false
            }
        }
        if (!drawFlicker && MathUtils.random(80) < 5) {
            drawFlicker = true
            maxFlickerTime = MathUtils.random(3f, 10f)
            dTDraw = maxFlickerTime
        }
        val r = 700f
        zoom = (Math.sin(delta.toDouble() * 25) * r).toFloat()
        zoomIn += 1
        zoom += 1
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
}