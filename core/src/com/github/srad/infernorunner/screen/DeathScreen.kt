package com.github.srad.infernorunner.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.github.srad.infernorunner.InfernoRunner
import com.github.srad.infernorunner.core.*

class DeathScreen(private val game: InfernoRunner) : AbstractScreen() {
    private val satanEndTexture: Texture by lazy { Resource.satanEndTexture.load }
    private val bumpMap: Texture by lazy { Resource.satanEndBumpTexture.load }

    lateinit var vertexShader: String
    lateinit var fragmentShader: String
    lateinit var shader: ShaderProgram

    override fun show() {
        super.show()
        vertexShader = Gdx.files.internal("shader/flicker_with_mouse/vertex.glsl").readString()
        fragmentShader = Gdx.files.internal("shader/flicker_with_mouse/fragment.glsl").readString()
        shader = ShaderProgram(vertexShader, fragmentShader)
        disposables.add(shader)
    }

    override fun draw(window: Window, modelBatch: ModelBatch, spriteBatch: SpriteBatch) {
        spriteBatch.begin()
        // TODO: Tried to set as uniforms, since values don't change
        // during render call. But doesn't reach fragment shader.
        shader.setAttributef("v_window", window.width, window.height, 0f, 0f)
        shader.setUniformi("invert", if (MathUtils.randomBoolean(0.1f)) 1 else 0)
        shader.setUniformf("dimming_ratio", 0f)
        shader.setUniformi("u_texture2", 1)
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1)
        bumpMap.bind()
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
        spriteBatch.shader = shader
        spriteBatch.draw(satanEndTexture, 0f, 0f, window.width, window.height)
        spriteBatch.end()
    }

    override fun handleInput(gameInfo: GameInfo, delta: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
            dispose()
            Gdx.app.exit()
        }
    }
}