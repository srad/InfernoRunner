package com.github.srad.infernorunner.entity

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape
import com.github.srad.infernorunner.core.CollisionMasks
import com.github.srad.infernorunner.core.ITeleporter
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.animation.RotationAnimation
import com.github.srad.infernorunner.entity.base.AbstractPhysicalEntity
import com.github.srad.infernorunner.entity.base.Mass
import com.github.srad.infernorunner.entity.base.PhysicalAttributes
import com.github.srad.infernorunner.entity.player.PlayerEntity
import com.github.srad.infernorunner.level.IMappable
import com.github.srad.infernorunner.level.MapInfo

class PortalEntity : AbstractPhysicalEntity(Resource.portalModel,
        PhysicalAttributes(btCylinderShape(Vector3(5f, 0.5f, 5f)), Mass(0f), CollisionMasks(PlayerEntity::class))),
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