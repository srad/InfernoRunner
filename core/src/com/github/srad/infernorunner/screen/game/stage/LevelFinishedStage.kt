package com.github.srad.infernorunner.screen.game.stage

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.github.srad.infernorunner.core.GamePref
import com.github.srad.infernorunner.core.PrefType
import com.github.srad.infernorunner.entity.player.PlayerEntity
import com.github.srad.infernorunner.level.AbstractLevelCreator
import com.github.srad.infernorunner.screen.BloodyStage


interface ILevelFinishedStage {
    fun ok()
}

class LevelFinishedStage(private val playerEntity: PlayerEntity, private val settings: GamePref, private val level: AbstractLevelCreator, private val listener: ILevelFinishedStage) : BloodyStage("Level Completed") {
    override val hasControls = true

    override fun build() {
        super.build()

        val levelCol = Table()
        levelCol.add(Label("Level", style)).padBottom(30f).left().top().colspan(2)
        for (e in arrayOf(Pair("Level Finished", level.name), Pair("Lives", playerEntity.lives))) {
            val valueLabel = Label(e.second.toString(), infoStyle)
            levelCol.row().padBottom(15f)
            levelCol.add(Label(e.first, infoStyle)).left().padRight(30f).top().minWidth(300f)
            levelCol.add(valueLabel).left().top().padRight(30f)
        }
        root.add(levelCol).padRight(100f).top().left()

        val playerCol = Table()
        playerCol.add(Label("Player", style)).padBottom(30f).left().colspan(2).top()
        for (e in arrayOf(Pair("Souls", playerEntity.score), Pair("Lives", playerEntity.lives))) {
            val valueLabel = Label(e.second.toString(), infoStyle)
            playerCol.row().padBottom(15f)
            playerCol.add(Label(e.first, infoStyle)).left().padRight(30f).top().minWidth(300f)
            playerCol.add(valueLabel).left().padRight(30f).top()
        }
        root.add(playerCol).padRight(100f).top().left()

        val startCol = Table()
        startCol.add(Label("Statistics", style)).padBottom(30f).left().colspan(2).top()
        for (pref in PrefType.statistics) {
            val valueLabel = Label(settings.get(pref).toString(), infoStyle)
            startCol.row().padBottom(15f)
            startCol.add(Label(pref.label, infoStyle)).left().padRight(30f).minWidth(300f).top()
            startCol.add(valueLabel).left().padRight(30f).top()
        }
        root.add(startCol).top().left()

        root.row().fillY()

        val btnOk = createButton("Ok", object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                listener.ok()
            }
        })

        root.row().expandY()
        root.add(btnOk).bottom().left()
    }
}