package com.github.srad.infernorunner.screen.game.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector3
import com.github.srad.infernorunner.core.GamePref
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.player.PlayerInstance
import com.github.srad.infernorunner.level.AbstractLevelCreator
import com.github.srad.infernorunner.screen.AbstractStage
import com.github.srad.infernorunner.screen.game.LevelInfo

class HudStage(private val levelInfo: LevelInfo, private val playerInstance: PlayerInstance, private val level: AbstractLevelCreator, private val settings: GamePref) : AbstractStage() {
    override val hasControls = true

    private val topText = """|<ESC/START>   Back to Main-menu
                             |
                             |<TAB/SELECT> Map
                             |
                             |<SPACE/B>       Air""".trimMargin("|")

    private val debugInfo
        get() = """|Cam-pos:   ${playerInstance.cam.position}
                   |Model-pos: ${playerInstance.transform}
                   |Body-pos:  ${playerInstance.rigidBody.worldTransform.getTranslation(Vector3.Zero)}
                   |Velocity:  ${playerInstance.rigidBody.linearVelocity}
                   |Active:    ${playerInstance.rigidBody.isActive}
                   |Respawn:    ${playerInstance.respawn}
                   |Shop:    ${playerInstance.closeToShop}
                   |Portal:    ${playerInstance.currentTeleporter != null}
                   |State:    ${playerInstance.stateManager.state}""".trimMargin("|").replace("\n", "\n\n")

    private val hudText get() = "Level: ${levelInfo.level?.name}  /  Souls: ${playerInstance.score}"//  >  Name: ${settings.get(PrefType.PlayerName)}"

    private val demonSound: Sound by lazy { Resource.demonSound.load }
    private val bloodBackground: Texture by lazy { Resource.hudTexture.load }
    private val lifeTexture: Texture by lazy { Resource.lifeTexture.load }

    override fun drawImplementation() {
        batch.begin()
        batch.draw(bloodBackground, 0f, 0f, width, height * 0.25f)
        hudFont.draw(batch, hudText, 60f, 110f)
        infoFont.draw(batch, topText, 50f, height - 50f)
        val totalLifeWidth = playerInstance.lives * lifeTexture.width
        val offsetFromRight = (width - totalLifeWidth - 70)
        for (i in 0..(playerInstance.lives - 1)) {
            batch.draw(lifeTexture, offsetFromRight + (lifeTexture.width * i.toFloat()), 50f)
        }
        batch.end()
    }

    override fun debugDraw() {
        batch.begin()
        infoFont.draw(batch, debugInfo, width / 2, 450f)
        batch.end()
    }

    override fun reset() {
        demonSound.stop()
    }

    override fun show() {
        super.show()
        Gdx.input.isCursorCatched = true
    }
}