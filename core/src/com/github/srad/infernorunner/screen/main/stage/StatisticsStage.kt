package com.github.srad.infernorunner.screen.main.stage

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.github.srad.infernorunner.core.GamePref
import com.github.srad.infernorunner.core.PrefType
import com.github.srad.infernorunner.screen.BloodyStage


interface IStatisticsStageListener {
    fun back()
}

class StatisticsStage(private val listener: IStatisticsStageListener, private val settings: GamePref) : BloodyStage("Settings") {
    override val hasControls = true

    private val valueLabels = HashMap<String, Label>()

    override fun build() {
        super.build()

        for (pref in PrefType.statistics) {
            val valueLabel = Label(settings.get(pref).toString(), infoStyle)
            valueLabels[pref.typeName] = valueLabel
            root.row().padBottom(25f)
            root.add(Label(pref.label, infoStyle)).left().padRight(30f)
            root.add(valueLabel).left().padRight(30f)
        }

        val btnBack = createButton("Back", object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                listener.back()
            }
        })

        val btnReset = createButton("Reset Statistics", object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                for (pref in PrefType.statistics) {
                    settings.set(pref, 0)
                    valueLabels[pref.typeName]?.setText("0")
                }
            }
        })

        root.row().expandY()
        root.add(btnBack).bottom().left()
        root.add(btnReset).bottom().right()
    }

    override fun show() {
        super.show()
        for (pref in PrefType.statistics) {
            valueLabels[pref.typeName]?.setText(settings.get(pref).toString())
        }
    }
}