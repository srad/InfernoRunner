package com.github.srad.infernorunner.screen.main.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.TimeUtils
import com.github.srad.infernorunner.core.GameInfo
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.screen.BloodyStage

interface IMainMenuListener {
    fun play(level: Int)
    fun settings()
    fun story()
    fun about()
    fun quit()
    fun statistics()
}

class MainMenuStage(private val listener: IMainMenuListener) : BloodyStage() {
    override val hasControls = true

    private var tStart = TimeUtils.millis()
    private var started = false
    private var dTDraw = 0f
    private var maxFlickerTime = 0f
    private var drawFlicker = false
    private val bumpMap: Texture by lazy { Resource.startBackgroundBump.load }

    private lateinit var btnSettings: ImageTextButton
    private lateinit var btnQuit: ImageTextButton
    private lateinit var btnStory: ImageTextButton
    private lateinit var btnAbout: ImageTextButton
    private lateinit var btnStats: ImageTextButton

    private lateinit var mixColorShader: ShaderProgram

    override val backgroundResource = Resource.startBackground

    private var passedTime = 0f

    private val staticSound by lazy { Resource.static.load }
    private val creepyMusic by lazy { Resource.theme2.load }

    //lateinit var ouija: Texture

    override fun build() {
        super.build()
        ShaderProgram.pedantic = false
        val vertexShader = Gdx.files.internal("shader/flicker_with_mouse/vertex.glsl").readString()
        val fragmentShader = Gdx.files.internal("shader/flicker_with_mouse/fragment.glsl").readString()
        shader = ShaderProgram(vertexShader, fragmentShader)

        val mixColorVertex = Gdx.files.internal("shader/mix_with_background/vertex.glsl").readString()
        val mixColorFragment = Gdx.files.internal("shader/mix_with_background/fragment.glsl").readString()
        mixColorShader = ShaderProgram(mixColorVertex, mixColorFragment)
        if (mixColorShader.isCompiled) {
            logError("Shader compilation error: ${mixColorShader.log}")
        }
        //ouija = resourceManager.resource(Resource.ouija)

        creepyMusic.isLooping = true

        btnSettings = createButton("Settings", object : ClickListener(Input.Buttons.LEFT) {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                listener.settings()
            }
        })

        btnQuit = createButton("Quit", object : ClickListener(Input.Buttons.LEFT) {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                listener.quit()
            }
        })

        btnStory = createButton("Story", object : ClickListener(Input.Buttons.LEFT) {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                listener.story()
            }
        })

        btnAbout = createButton("About", object : ClickListener(Input.Buttons.LEFT) {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                listener.about()
            }
        })

        btnStats = createButton("Statistics", object : ClickListener(Input.Buttons.LEFT) {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                listener.statistics()
            }
        })

        root.pad(20f).right().bottom()
        root.setFillParent(true)
        for (f in Gdx.files.internal("level").list()) {
            root.row()
            root.add(createButton("Level ${f.nameWithoutExtension()}", object : ClickListener(Input.Buttons.LEFT) {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    listener.play(f.nameWithoutExtension().toInt())
                }
            }))
        }
        root.row()
        root.add(btnStats)
        root.row()
        root.add(btnSettings)
        root.row()
        root.add(btnStory)
        root.row()
        root.add(btnAbout)
        root.row()
        root.add(btnQuit)
        addActor(root)
    }

    override fun update(delta: Float) {
        if (!started && TimeUtils.timeSinceMillis(tStart) > 6000) {
            started = true
            Gdx.input.isCursorCatched = true
            staticSound.loop(0.05f)
        } else {
            passedTime += delta
        }

        if (drawFlicker) {
            dTDraw -= 1
            if (dTDraw <= 0) {
                drawFlicker = false
            }
        }
        if (!drawFlicker && MathUtils.random(80) < 5) {
            drawFlicker = true
            maxFlickerTime = MathUtils.random(3f, 10f)
            dTDraw = maxFlickerTime
        }
    }

    override fun handleInput(gameInfo: GameInfo, delta: Float) {
        super.handleInput(gameInfo, delta)
        if (started) {
            if (gameInfo.controller.analogLeft.isMoving) {
                if (gameInfo.controller.analogLeft.left || gameInfo.controller.analogLeft.right) {
                    Gdx.input.setCursorPosition(Gdx.input.x + gameInfo.controller.analogLeft.horizontal.scaleWithSign.toInt() * 13, Gdx.input.y)
                }
                if (gameInfo.controller.analogLeft.up || gameInfo.controller.analogLeft.down) {
                    Gdx.input.setCursorPosition(Gdx.input.x, Gdx.input.y + gameInfo.controller.analogLeft.vertical.scaleWithSign.toInt() * 13)
                }
            }
        }
    }

    override fun draw() {
        batch.begin()
        if (started) {
            batch.shader = shader
            // TODO: Tried to set as uniforms, since values don't change
            // during render call. But doesn't reach fragment shader.
            shader?.setAttributef("v_mouse", Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f, 0f)
            shader?.setAttributef("v_window", width, height, 0f, 0f)
        } else {
            batch.shader = mixColorShader
            batch.shader.setUniformf("passed_time", passedTime)
        }
        batch.shader.setUniformi("invert", if (MathUtils.randomBoolean(0.01f)) 1 else 0)
        batch.shader.setUniformf("dimming_ratio", if (drawFlicker) (dTDraw / maxFlickerTime) else 1f)
        batch.shader.setUniformi("u_texture2", 1)
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1)
        bumpMap.bind()
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
        batch.end()

// Show ouija cursor, but interferes with shader, so nah.
//        if (started) {
//            spriteBatch.begin()
//            spriteBatch.shader = SpriteBatch.createDefaultShader()
//            val y = Gdx.input.y.toFloat() + 200
//            spriteBatch.draw(ouija, Gdx.input.x.toFloat() - ouija.width / 2, if (y < (window.height / 2)) window.height - y else -y + window.height)
//            spriteBatch.end()
//        }

        super.draw()
    }

    override fun show() {
        super.show()
        tStart = TimeUtils.millis()
        creepyMusic.play()
        staticSound.resume()
        if (started) {
            Gdx.input.isCursorCatched = true
        }
    }

    override fun hide() {
        super.hide()
        staticSound.pause()
        creepyMusic.pause()
    }
}