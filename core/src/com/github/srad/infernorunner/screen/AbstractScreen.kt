package com.github.srad.infernorunner.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.core.Window
import com.github.srad.infernorunner.entity.AbstractGameCycle

abstract class AbstractScreen : AbstractGameCycle(), Screen {
    protected val stage = Stage(ScreenViewport())

    // https://www.dafont.com/burtons-nightmare.font
    private val generator = FreeTypeFontGenerator(Gdx.files.internal("font/NITEMARE.TTF"))
    private val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
    private lateinit var fontTitle: BitmapFont

    protected lateinit var currentStage: AbstractStage

    // Created with: https://dabuttonfactory.com/
    private val atlas: TextureAtlas by lazy { Resource(com.github.srad.infernorunner.GameConfig.buttonAtlas, TextureAtlas::class).load }

    private var _isVisible = false
    val isVisible get() = _isVisible

    override fun show() {
        super.create()

        hasShowed = true
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow)

        parameter.size = 100
        parameter.color = Color.RED
        fontTitle = generator.generateFont(parameter)

        listenInput()
        _isVisible = true
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        stage.viewport.update(width, height, true)
        stage.viewport.setScreenSize(width, height)
    }

    override fun render(delta: Float) {
        render()
    }

    protected fun catchCursor(doIt: Boolean = true) {
        Gdx.input.isCursorCatched = doIt
    }

    override fun update(delta: Float) {
        super.update(delta)
        currentStage.update(delta)
    }

    override fun draw(window: Window, modelBatch: ModelBatch, spriteBatch: SpriteBatch) {
        super.draw(window, modelBatch, spriteBatch)
        if (currentStage.isVisible) {
            currentStage.draw()
        }
    }

    protected fun showStage(stage: AbstractStage) {
        if (currentStage == stage) {
            return
        }
        inputManager.clearInput()
        currentStage.switch()
        currentStage.hide()
        stage.show()
        currentStage = stage
    }

    fun listenInput() {
        Gdx.input.inputProcessor = inputManager
    }

    override fun hide() {
        pause()
        currentStage.hide()
        _isVisible = false
    }

    protected open fun createButton(text: String, clickListener: ClickListener): ImageTextButton {
        val p = FreeTypeFontGenerator.FreeTypeFontParameter()
        p.color = Color.WHITE
        p.size = 35

        val fontButton = generator.generateFont(p)

        val region = atlas.findRegion("default")
        val btn = ImageTextButton(text, with(ImageTextButton.ImageTextButtonStyle()) {
            up = TextureRegionDrawable(atlas.findRegion("default"))
            down = TextureRegionDrawable(atlas.findRegion("default-on"))
            font = fontButton
            this
        })
        btn.height = region.originalHeight.toFloat() * 1.5f
        btn.width = region.originalWidth.toFloat() * 2

        btn.addListener(clickListener)

        return btn
    }
}