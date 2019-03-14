package com.github.srad.infernorunner.screen.game.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector3
import com.github.srad.infernorunner.core.GamePref
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.player.PlayerEntity
import com.github.srad.infernorunner.level.AbstractLevelCreator
import com.github.srad.infernorunner.screen.AbstractStage
import com.github.srad.infernorunner.screen.game.LevelInfo

class HudStage(private val levelInfo: LevelInfo, private val playerEntity: PlayerEntity, private val level: AbstractLevelCreator, private val settings: GamePref) : AbstractStage() {
    override val hasControls = true

    private val topText = """|<ESC/START>   Back to Main-menu
                             |
                             |<TAB/SELECT> Map
                             |
                             |<SPACE/B>       Air""".trimMargin("|")

    private val debugInfo
        get() = """|Cam-pos:   ${playerEntity.cam.position}
                   |Model-pos: ${playerEntity.transform}
                   |Body-pos:  ${playerEntity.rigidBody.worldTransform.getTranslation(Vector3.Zero)}
                   |Velocity:  ${playerEntity.rigidBody.linearVelocity}
                   |Active:    ${playerEntity.rigidBody.isActive}
                   |Respawn:    ${playerEntity.respawn}
                   |Shop:    ${playerEntity.closeToShop}
                   |Portal:    ${playerEntity.currentTeleporter != null}
                   |Behavior:    ${playerEntity.behaviorManager.behavior}""".trimMargin("|").replace("\n", "\n\n")

    private val hudText get() = "Level: ${levelInfo.level?.name}  /  Souls: ${playerEntity.score}"//  >  Name: ${settings.get(PrefType.PlayerName)}"

    private val demonSound: Sound by lazy { Resource.demonSound.load }
    private val bloodBackground: Texture by lazy { Resource.hudTexture.load }
    private val lifeTexture: Texture by lazy { Resource.lifeTexture.load }

    override fun drawImplementation() {
        batch.begin()

        batch.draw(bloodBackground, 0f, height - (height * 0.18f), width, height * 0.18f)
        hudFont.draw(batch, hudText, 50f, height - 30f)
        infoFont.draw(batch, topText, width - 450f, 120f)

        val totalLifeWidth = playerEntity.lives * lifeTexture.width
        val offsetFromRight = (width - totalLifeWidth - 70)

        for (i in 0..(playerEntity.lives - 1)) {
            batch.draw(lifeTexture, offsetFromRight + (lifeTexture.width * i.toFloat()), height - lifeTexture.height - 40f)
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