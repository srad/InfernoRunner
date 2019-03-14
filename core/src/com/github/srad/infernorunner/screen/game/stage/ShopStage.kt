package com.github.srad.infernorunner.screen.game.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.github.srad.infernorunner.entity.player.PlayerEntity
import com.github.srad.infernorunner.screen.BloodyStage

interface IShopListener {
    fun open() {}
    fun close() {}
    fun purchase() {}
}

class ShopStage(private val playerEntity: PlayerEntity, private val listener: IShopListener) : BloodyStage("Merchant") {
    override val hasControls = true
    private var renderShop = false
    override val playMusic = true

    private var isShopOpened = false
    private var hideShop = false
    private lateinit var btnExitShop: ImageTextButton

    private lateinit var labelLives: Label
    private lateinit var labelSouls: Label

    override fun build() {
        super.build()

        btnExitShop = createButton("Leave", object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                listener.close()
            }
        })

        root.setFillParent(true)
        root.padTop(30f).padLeft(60f).padRight(60f).padBottom(60f)
        viewport.update(Gdx.graphics.width, Gdx.graphics.height)

        labelSouls = Label("Souls to spend: ${playerEntity.score}", style)
        root.add(labelSouls).left().top().padBottom(50f)
        root.row()

        val l1 = Label("More Live", style)
        val btn = createButton("Summon", object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (playerEntity.score > 0) {
                    bumpSound.play()
                    playerEntity.incLive(1)
                    playerEntity.incScore(-1)
                    listener.purchase()
                }
                refresh()
            }
        })
        root.add(l1).padRight(100f).left()
        root.add(btn).left()
        root.row()

        val l2 = Label("Heal Injury", style)
        val btn2 = createButton("Summon", object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (playerEntity.score > 0) {
                    bumpSound.play()
                    playerEntity.incScore(-1)
                    playerEntity.removeBloodStain()
                    listener.purchase()
                }
                refresh()
            }
        })
        root.add(l2).padRight(100f).left()
        root.add(btn2).left()
        root.row()
        root.add().colspan(2).padBottom(50f)
        root.row()
        root.add(Label("Stats: ", style)).left().top().padRight(50f)
        labelLives = Label("Lives: ${playerEntity.lives}", style)
        root.add(labelLives).left().top()
        root.row()
        root.add().colspan(2).padBottom(100f)
        root.row()
        root.add(btnExitShop).left().bottom().expandY()

        root.center().left().top()
        root.isVisible = false
        addActor(root)
    }

    override fun update(delta: Float) {
        if (playerEntity.closeToShop && !isShopOpened) {
            show()
            refresh()
            bumpSound.play()
            isShopOpened = true
            renderShop = true
            listener.open()
        }
        if (hideShop && !playerEntity.closeToShop) {
            hideShop = false
            renderShop = false
            isShopOpened = false
        }
    }

    override fun hide() {
        super.hide()
        bumpSound.play()
        hideShop = true
    }

    fun refresh() {
        labelLives.setText("Lives: ${playerEntity.lives}/${playerEntity.maxLives}")
        labelSouls.setText("Souls to spend: ${playerEntity.score}")
    }

    override fun show() {
        super.show()
        Gdx.input.inputProcessor = this
    }
}