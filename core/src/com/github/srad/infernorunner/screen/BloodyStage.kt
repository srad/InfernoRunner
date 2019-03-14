package com.github.srad.infernorunner.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.github.srad.infernorunner.core.GameInfo
import com.github.srad.infernorunner.core.Resource

/** Has a down-sliding bloodTexture background for all stages. */
abstract class BloodyStage(private val title: String? = null, listener: IStageListener? = null) : AbstractStage(listener) {
    protected val root = Table()

    // Controller
    private var buttonCount = 0
    private var buttonIndex = -1
    private var isMoving = false
    private var selectedBtn: ImageTextButton? = null
    private var buttonDown = false

    private var selectedFirstButtonOnShow = false

    // Animation
    private val startOffset = 20f
    private var slideDownOffset = startOffset

    protected open val backgroundResource = Resource.bloodBackgroundCrossed
    private lateinit var bg: Texture

    protected var shader: ShaderProgram? = null

    override fun build() {
        bg = backgroundResource.load
        viewport.update(Gdx.graphics.width, Gdx.graphics.height)
        root.setFillParent(true)
        root.left().top()
        root.padTop(30f).padLeft(60f).padRight(60f).padBottom(60f)

        root.row()
        if (title != null) {
            root.add(Label(title, style)).left().top().padBottom(20f)
            root.row()
        }

        root.isVisible = false
        addActor(root)
    }

    override fun drawImplementation() {
        if (slideDownOffset < height) {
            slideDownOffset += slideDownOffset * .15f
        } else if (!root.isVisible) {
            slideDownOffset = height
            root.isVisible = true
            bumpSound.play()
        }

        batch.apply {
            begin()
            enableBlending()
            draw(bg, 0f - zoom, height - slideDownOffset - zoom, width + zoom * 2, height + zoom * 2f)
            end()
        }
    }

    override fun handleInput(gameInfo: GameInfo, delta: Float) {
        if (hasControls) {
            if (!gameInfo.controller.analogRight.isMoving) {
                isMoving = false
            }
            if (gameInfo.controller.analogRight.isMoving && !isMoving || !selectedFirstButtonOnShow) {
                isMoving = true
                val btns: List<ImageTextButton> = root.children
                        .filter { c -> c is ImageTextButton }
                        .map { c -> c as ImageTextButton }

                if (btns.isNotEmpty()) {
                    buttonCount = btns.size
                    val lastIndex = Math.max(0, buttonIndex)
                    val lastBtn = btns[lastIndex]
                    lastBtn.isChecked = false

                    if (gameInfo.controller.analogRight.down || gameInfo.controller.analogRight.right || !selectedFirstButtonOnShow) {
                        buttonIndex = Math.min(buttonCount - 1, buttonIndex + 1)
                        selectedFirstButtonOnShow = true
                    }
                    if (gameInfo.controller.analogRight.up || gameInfo.controller.analogRight.left) {
                        buttonIndex = Math.max(0, buttonIndex - 1)
                    }
                    selectedBtn = btns[buttonIndex]
                    selectedBtn?.isChecked = true
                }
            }

            if (!gameInfo.controller.a) {
                buttonDown = false
            }

            if (!buttonDown && selectedBtn != null && gameInfo.controller.a) {
                buttonDown = true
                selectedBtn?.listeners?.forEach { l -> (l as ClickListener).clicked(null, 0f, 0f) }
            }
        }
    }

    private fun uncheckButtons() = root.children
            .filter { c -> c is ImageTextButton }
            .map { c -> c as ImageTextButton }
            .forEach { c -> c.isChecked = false }

    override fun show() {
        super.show()
        uncheckButtons()
        buttonIndex = -1
        selectedFirstButtonOnShow = false
    }
}