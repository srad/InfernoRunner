package com.github.srad.infernorunner.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.srad.infernorunner.core.*

interface IStageListener {
    fun show() {}
    fun hide() {}
}

abstract class AbstractStage(private val listener: IStageListener? = null) : Stage(ScreenViewport()), ILoggable, IGameLoopListener {
    open val hasControls = false
    open val playMusic = false

    private val gameFontGenerator = FreeTypeFontGenerator(Gdx.files.internal("font/NITEMARE.TTF"))
    private val infoFontGenerator = FreeTypeFontGenerator(Gdx.files.internal("font/digital.ttf"))
    private val textFontGenerator = FreeTypeFontGenerator(Gdx.files.internal("font/BLKCHCRY.TTF"))

    private val generator = FreeTypeFontGenerator(Gdx.files.internal("font/NITEMARE.TTF"))
    private val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()

    protected lateinit var hudFont: BitmapFont
    protected lateinit var infoFont: BitmapFont
    protected lateinit var textFont: BitmapFont

    protected val inputManager = InputManager()

    private var init = false

    var isVisible = false

    private val inputMultiplexer = InputMultiplexer()

    protected lateinit var style: Label.LabelStyle
    protected lateinit var infoStyle: Label.LabelStyle
    protected lateinit var textStyle: Label.LabelStyle

    protected val bumpSound: Sound by lazy { Resource.bump.load }
    protected val keySound: Sound by lazy { Resource.key.load }
    private val music: Music by lazy { Resource.theme2.load }

    private val atlas by lazy { Resource.buttonAtlas.load }

    open val infoFontSize = 9
    open val textFontSize = 25
    open val hudFontSize = 40

    protected var zoom = 0f

    override fun create() {
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow)
        Gdx.input.setCursorPosition((width / 2).toInt() - 50, (height / 2).toInt() - 50)

        if (init) {
            return
        }

        parameter.size = 100
        parameter.color = Color.RED

        hudFont = generateFont(gameFontGenerator, hudFontSize, Color.BLACK, true)
        infoFont = generateFont(infoFontGenerator, infoFontSize, Color.WHITE, false)
        textFont = generateFont(textFontGenerator, textFontSize, Color.BLACK, true)
        style = Label.LabelStyle(hudFont, Color.WHITE)
        infoStyle = Label.LabelStyle(infoFont, Color.WHITE)
        textStyle = Label.LabelStyle(textFont, Color.WHITE)
        build()
        Controllers.addListener(inputManager)
        init = true
    }

    protected open fun createButton(text: String, clickListener: ClickListener): ImageTextButton {
        val p = FreeTypeFontGenerator.FreeTypeFontParameter()
        p.color = Color.WHITE
        p.size = 28

        val fontButton = generator.generateFont(p)

        val btn = ImageTextButton(text, with(ImageTextButton.ImageTextButtonStyle()) {
            up = TextureRegionDrawable(atlas.findRegion("default"))
            down = TextureRegionDrawable(atlas.findRegion("default-on"))
            font = fontButton
            over = TextureRegionDrawable(atlas.findRegion("default-on"))
            checked = TextureRegionDrawable(atlas.findRegion("default-on"))
            this
        })
        btn.align(Align.top)
        btn.addListener(clickListener)

        return btn
    }

    private val defaultShader by lazy { SpriteBatch.createDefaultShader() }

    override fun draw() {
        if (isVisible) {
            handleInput(inputManager.gameInfo, Gdx.graphics.deltaTime)
            drawImplementation()
            batch.shader = defaultShader
            super<Stage>.draw()
            super.act()
        }
    }

    private fun generateFont(g: FreeTypeFontGenerator, size: Int, color: Color, whiteBorders: Boolean = false): BitmapFont {
        parameter.size = size
        parameter.color = color
        parameter.shadowOffsetX = 2
        parameter.shadowOffsetY = 2
        parameter.shadowColor = Color.BLACK
        parameter.borderWidth = 2f
        parameter.borderColor = if (!whiteBorders) Color.BLACK else Color.WHITE
        return g.generateFont(parameter)
    }

    override fun dispose() {
        try {
            super.dispose()
        } catch (ex: Exception) {
            logError("disposing-stage", ex.message.toString())
        }
    }

    protected open fun build() {}
    open fun drawImplementation() {}
    open fun reset() {}
    open fun debugDraw() {}
    open fun show() {
        create()
        zoom = 0f
        isVisible = true
        if (hasControls) {
            inputMultiplexer.addProcessor(this)
            inputMultiplexer.addProcessor(inputManager)
            Gdx.input.inputProcessor = inputMultiplexer
            Gdx.input.isCursorCatched = false
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow)
        } else {
            Gdx.input.isCursorCatched = true
        }
        listener?.show()
        if (playMusic) {
            music.play()
        }
    }

    open fun hide() {
        isVisible = false
        inputMultiplexer.removeProcessor(this)
        inputMultiplexer.removeProcessor(inputManager)
        listener?.hide()
        if (playMusic) {
            music.stop()
        }
    }

    fun switch() {
        keySound.play()
    }
}

