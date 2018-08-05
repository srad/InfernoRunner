package com.github.srad.infernorunner.screen.main.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.github.srad.infernorunner.screen.BloodyStage

interface IAboutListener {
    fun back()
}

class AboutStage(private val listener: IAboutListener) : BloodyStage("About") {
    private lateinit var btnBack: ImageTextButton
    override val hasControls = true

    override fun build() {
        super.build()

        root.add(Label("Asset sources:", infoStyle)).left().top().padBottom(20f)
        root.row()
        root.add(Label(Gdx.files.internal("license.md").readString().replace("\n", "\n\n"), infoStyle)).left()
        root.row()

        btnBack = createButton("Back", object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
               listener.back()
            }
        })
        root.add(btnBack).expandY().bottom().left()
    }
}
