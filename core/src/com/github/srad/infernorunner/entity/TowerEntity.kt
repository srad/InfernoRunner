package com.github.srad.infernorunner.entity

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.github.srad.infernorunner.core.CollisionMasks
import com.github.srad.infernorunner.core.IModelSpawner
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.base.*
import com.github.srad.infernorunner.entity.player.IPlayerUpdateListener
import com.github.srad.infernorunner.entity.player.PlayerEntity
import com.github.srad.infernorunner.level.IMappable
import com.github.srad.infernorunner.level.MapInfo

class TowerEntity : AbstractPhysicalEntity(Resource.towerModel,
        PhysicalAttributes(
                BoxShape(2f, 18f, 2f), Mass(1f),
                CollisionMasks(PlayerEntity::class, PhysicalBlockEntity::class))),
        IMappable,
        IPlayerUpdateListener,
        IModelSpawner {
    override val name = "Tower"
    override val mapInfo = MapInfo("Tower", Color.GRAY)
    override var spawnModel = false

    private var shootingInterval = 1f
    private val range = 2500f
    private val attackSound: Sound by lazy { Resource.towerAttack.load }

    override fun updatePlayer(delta: Float, playerEntity: PlayerEntity) {
        val me = this
        val target = playerEntity

        val towards = target.translation.sub(me.translation)
        if (towards.len2() < range && shootingInterval > 5F) {
            spawnModel = true
            attackSound.play()
            shootingInterval = 0F
        } else {
            shootingInterval += delta
        }
    }

    override fun spawn(): AbstractEntity {
        val m = ProjectTileEntity()
        m.transform.setTranslation(translation.add(0f, 9f, 0f))
        m.transform.scl(0.5f)
        return m
    }
}

