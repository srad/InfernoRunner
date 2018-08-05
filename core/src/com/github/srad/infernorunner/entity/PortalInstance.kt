package com.github.srad.infernorunner.entity

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape
import com.github.srad.infernorunner.core.CollisionMasks
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.player.PlayerInstance
import com.github.srad.infernorunner.level.IMappable
import com.github.srad.infernorunner.level.MapInfo

class PortalInstance : PhysicalModelInstance(Resource.portalModel, PhysicalAttributes(btCylinderShape(Vector3(5f, 0.5f, 5f)), 0f, CollisionMasks(PlayerInstance::class))), IMappable {
    override val name = "Portal"
    override val mapInfo = MapInfo("Portal", Color.BLUE)

    val sound: Sound by lazy { Resource.laserSound.load }

    var rotate = true
    private val speed = 7f

    override fun update(delta: Float) {
        if (rotate) {
            rigidBody.worldTransform = rigidBody.worldTransform.rotateRad(Vector3.Y, speed * delta)
        }
    }
}