package com.github.srad.infernorunner.screen.main.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.github.srad.infernorunner.screen.BloodyStage

interface IStoryStageListener {
    fun back()
}

class StoryStage(private val listener: IStoryStageListener) : BloodyStage("Story") {
    private lateinit var btnBack: ImageTextButton
    override val hasControls = true

    private val story = """Your are dead but you find yourself awake in hell.
        |
        |Since you are a disgusting terrible human being,
        |God does not have a place for you in his kingdom.
        |
        |Even the devil himself is not so sure about you.
        |He wants you to prove that you are worth his attention by
        |completing challenges in hell to be welcomed in hell.
        |
        |If you fail your soul is void.
    """.trimMargin("|")

    override fun build() {
        super.build()

        val label = Label(story, textStyle)
        label.setWrap(true)
        root.add(label).left().width(width / 2)
        root.row()

        btnBack = createButton("Back", object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                listener.back()
            }
        })
        root.add(btnBack).expandY().bottom().left()
    }

    override fun show() {
        super.show()
        Gdx.input.isCursorCatched = false
    }
}