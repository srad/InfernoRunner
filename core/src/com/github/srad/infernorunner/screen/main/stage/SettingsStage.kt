package com.github.srad.infernorunner.screen.main.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.github.srad.infernorunner.core.GamePref
import com.github.srad.infernorunner.core.PrefType
import com.github.srad.infernorunner.screen.BloodyStage


interface ISettingsStageListener {
    fun back()
    fun changePlayerSettings(name: String)
}

class SettingsStage(private val listener: ISettingsStageListener, private val settings: GamePref) : BloodyStage("Settings") {
    override val hasControls = true

    private lateinit var playerName: Label
    private lateinit var volumeMusicLabel: Label
    private lateinit var volumeSoundLabel: Label
    private lateinit var musicOnOffLabel: Label
    private lateinit var soundOnOffLabel: Label

    lateinit var textfieldPlayerName: TextField
    lateinit var checkboxSound: CheckBox
    lateinit var checkboxMusic: CheckBox
    lateinit var sliderSound: Slider
    lateinit var sliderMusic: Slider

    private val skin: Skin = Skin(Gdx.files.internal("uiskins/shade/skin/uiskin.json"))

    override fun build() {
        super.build()

        playerName = Label("Player Name", infoStyle)
        volumeMusicLabel = Label("Music Volume", infoStyle)
        volumeSoundLabel = Label("Sound Volume", infoStyle)
        musicOnOffLabel = Label("Music Enabled", infoStyle)
        soundOnOffLabel = Label("Sound Enabled", infoStyle)

        textfieldPlayerName = TextField(settings.get(PrefType.PlayerName), skin)
        textfieldPlayerName.maxLength = 20
        textfieldPlayerName.setTextFieldListener { _, _ ->
            if (textfieldPlayerName.text.isNotEmpty()) {
                settings.set(PrefType.PlayerName, textfieldPlayerName.text)
            }
        }

        checkboxSound = CheckBox(null, skin)
        checkboxSound.isChecked = settings.get(PrefType.SoundEnabled)
        checkboxSound.addListener {
            settings.set(PrefType.SoundEnabled, checkboxSound.isChecked)
            false
        }

        checkboxMusic = CheckBox(null, skin)
        checkboxMusic.isChecked = settings.get(PrefType.MusicEnabled)
        checkboxMusic.addListener {
            settings.set(PrefType.MusicEnabled, checkboxMusic.isChecked)
            false
        }

        sliderSound = Slider(0f, 1f, 0.1f, false, skin)
        sliderSound.value = settings.get(PrefType.SoundVolume)
        sliderSound.addListener {
            settings.set(PrefType.SoundVolume, sliderSound.value)
            false
        }

        sliderMusic = Slider(0f, 1f, 0.1f, false, skin)
        sliderMusic.value = settings.get(PrefType.MusicVolume)
        sliderMusic.addListener {
            settings.set(PrefType.MusicVolume, sliderMusic.value)
            false
        }

        root.row().padBottom(20f)
        root.add(playerName).left().padRight(30f)
        root.add(textfieldPlayerName).left().padRight(30f)
        root.row().padBottom(20f)
        root.add(volumeMusicLabel).left().padRight(30f)
        root.add(sliderMusic).left()
        root.row().padBottom(20f)
        root.add(musicOnOffLabel).left().padRight(30f)
        root.add(checkboxMusic).left()
        root.row().padBottom(20f)
        root.add(volumeSoundLabel).left().padRight(30f)
        root.add(sliderSound).left()
        root.row().padBottom(20f)
        root.add(soundOnOffLabel).left().padRight(30f)
        root.add(checkboxSound).left()
        root.row().padBottom(20f)

        val btnBack = createButton("Back", object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                listener.back()
            }
        })

        root.row().expandY()
        root.add(btnBack).bottom().left()
    }

    override fun show() {
        super.show()
        Gdx.input.isCursorCatched = false
    }
}