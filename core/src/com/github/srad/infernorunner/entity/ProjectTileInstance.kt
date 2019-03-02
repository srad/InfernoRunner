package com.github.srad.infernorunner.entity

import com.badlogic.gdx.math.Vector3
import com.github.srad.infernorunner.core.Resource
import com.github.srad.infernorunner.entity.player.IPlayerUpdateListener
import com.github.srad.infernorunner.entity.player.PlayerInstance

class ProjectTileInstance : AbstractModelInstance(Resource.projectileModel), IPlayerUpdateListener {
    override val name = "Projectile"

    var towards: Vector3? = null
    var isHoming = Math.random() < 0.2

    private var currentFlyingTime: Float = 0F
    private var maxFlyingTime: Float = 3F

    private val projectileSpeed: Float = 0.8f

    override fun updatePlayer(delta: Float, playerInstance: PlayerInstance) {
        val me = this
        val target = playerInstance
        val distance = target.translation.sub(me.translation)

        if (towards == null || isHoming) {
            towards = distance.cpy().nor().scl(1 / projectileSpeed)
            if (towards!!.len2() > distance.len2()) {
                towards = distance
            }
        }

        currentFlyingTime += delta

        if (currentFlyingTime < maxFlyingTime) {
            if (distance!!.len2() > 0.1f) {
                transform.translate(towards)
            } else {
                playerInstance.addBloodStain()
                alive = false
                remove = true
            }
        } else {
            alive = false
            remove = true
        }
    }
}
