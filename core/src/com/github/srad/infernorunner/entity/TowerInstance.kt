package com.github.srad.infernorunner.entity

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.github.srad.infernorunner.core.CollisionMasks
import com.github.srad.infernorunner.core.IModelSpawner
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.player.IPlayerUpdateListener
import com.github.srad.infernorunner.entity.player.PlayerInstance
import com.github.srad.infernorunner.level.IMappable
import com.github.srad.infernorunner.level.MapInfo

class TowerInstance : PhysicalModelInstance(Resource.towerModel,
        PhysicalAttributes(btBoxShape(Vector3(2f, 18f, 2f)), 1f, CollisionMasks(PlayerInstance::class, PhysicalBlockInstance::class))),
        IMappable, IPlayerUpdateListener, IModelSpawner {
    override val name = "Tower"
    override val mapInfo = MapInfo("Tower", Color.GRAY)
    override var spawnModel = false

    private var shootingInterval = 1f
    private val range = 2500f
    private val attackSound: Sound by lazy { Resource.towerAttack.load }

    override fun updatePlayer(delta: Float, playerInstance: PlayerInstance) {
        val me = this
        val target = playerInstance

        val towards = target.translation.sub(me.translation)
        if (towards.len2() < range && shootingInterval > 5F) {
            spawnModel = true
            attackSound.play()
            shootingInterval = 0F
        } else {
            shootingInterval += delta
        }
    }

    override fun spawn(): AModelInstance {
        val m = ProjectTileInstance()
        m.transform.setTranslation(translation.add(0f, 9f, 0f))
        m.transform.scl(0.5f)
        return m
    }
}

