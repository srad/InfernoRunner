package com.github.srad.infernorunner.screen.game.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.github.srad.infernorunner.core.GamePref
import com.github.srad.infernorunner.core.PrefType
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.level.AbstractLevelCreator
import com.github.srad.infernorunner.screen.BloodyStage
import com.github.srad.infernorunner.screen.IStageListener


class InfoStage(private val level: AbstractLevelCreator, private val settings: GamePref, listener: IStageListener) : BloodyStage(null, listener) {
    private lateinit var map: Image
    override val backgroundResource = Resource.bloodBackground

    private val valueLabels = HashMap<String, Label>()

    override fun build() {
        super.build()

        root.setFillParent(true)
        viewport.update(Gdx.graphics.width, Gdx.graphics.height)

        val mapdata = level.mapData
        val legends = mapdata.vectorWithMapInfo.groupBy { m -> m.second.legend }

        root.add(Label("Map", style)).colspan(legends.size * 2 + 1).padBottom(30f).left()
        root.add(Label("Statistics", style)).padBottom(30f).left().padLeft(100f)
        root.row()
        map = Image(mapdata.map)
        root.add(map).colspan(legends.size * 2).padBottom(30f)

        val legendSubTable = Table()

        for (legend in legends) {
            val p = Pixmap(32, 32, Pixmap.Format.RGBA8888)
            p.setColor(Color.BLACK)
            p.fillRectangle(0, 0, 32, 32)
            p.setColor(legend.value.first().second.color)
            p.fillRectangle(3, 3, 26, 26)
            legendSubTable.add(Image(Texture(p))).padRight(30f).padBottom(10f).left()
            legendSubTable.add(Label(legend.key.toUpperCase(), infoStyle)).left().padBottom(10f)
            legendSubTable.row()
        }
        root.add(legendSubTable).top().padLeft(10f)

        addStats()

        root.row()

        root.center().left().top()
        root.isVisible = false
        addActor(root)
    }

    private fun addStats() {
        val t = Table()

        for (pref in PrefType.statistics) {
            val valueLabel = Label(settings.get(pref).toString(), infoStyle)
            valueLabels[pref.typeName] = valueLabel
            t.row().padBottom(25f)
            t.add(Label(pref.label, infoStyle)).left().padRight(30f)
            t.add(valueLabel).left().padRight(30f)
        }

        root.add(t).top().padLeft(100f)
    }

    override fun show() {
        super.show()
        map.drawable = SpriteDrawable(Sprite(level.mapData.map))
        for (pref in PrefType.statistics) {
            valueLabels[pref.typeName]?.setText(settings.get(pref).toString())
        }
    }
}