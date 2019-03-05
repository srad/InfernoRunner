package com.github.srad.infernorunner.entity

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape
import com.github.srad.infernorunner.core.CollisionMasks
import com.github.srad.infernorunner.core.ITeleporter
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.player.PlayerInstance
import com.github.srad.infernorunner.level.IMappable
import com.github.srad.infernorunner.level.MapInfo

class PortalInstance : PhysicalModelInstance(Resource.portalModel,
        PhysicalAttributes(btCylinderShape(Vector3(5f, 0.5f, 5f)), 0f, CollisionMasks(PlayerInstance::class))),
        IMappable, ITeleporter {
    override val name = "Portal"
    override val mapInfo = MapInfo("Portal", Color.BLUE)

    override val destination get() = translation

    val sound: Sound by lazy { Resource.laserSound.load }

    override fun create() {
        super.create()
        addController(RotationAnimation::class)
    }

    override fun teleportStarted() {
        sound.play()
    }
}