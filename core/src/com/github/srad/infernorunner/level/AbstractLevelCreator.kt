package com.github.srad.infernorunner.level

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.github.srad.infernorunner.core.EntityManager
import com.github.srad.infernorunner.entity.*
import com.github.srad.infernorunner.entity.base.AbstractEntity
import com.github.srad.infernorunner.entity.player.PlayerEntity
import com.badlogic.gdx.utils.Array as GdxArray

abstract class AbstractLevelCreator(private val entityManager: EntityManager, val environment: Environment, val name: String) {
    companion object {
        fun randomColor(): Color {
            val r = MathUtils.random(0, 30)

            if (r < 14) return Color.RED
            if (r < 18) return Color.YELLOW
            if (r < 24) return Color.CORAL
            return if (r < 27) Color.ORANGE
            else Color.GREEN
        }
    }

    val entities = GdxArray<AbstractEntity>()

    abstract fun implementation()

    fun build(playerEntity: PlayerEntity) {
        implementation()
        // Mark closest spawn as start.
        val startSpawn = entityManager
                .filter { e -> e is CoffinEntity }
                .map { e -> Pair(e, playerEntity.translation.sub(e.translation)) }
                .sortedBy { pair -> pair.second.len() }
                .first().first as CoffinEntity

        startSpawn.reachedByPlayer = true
        playerEntity.rigidBody.worldTransform = startSpawn.transform.cpy().translate(1f, 6f, 0f)
    }

    protected fun renderSphere(minRadius: Float = 65f, maxRadius: Float = 75f) {
        // Draw sphere
        val radiusUp = MathUtils.random(minRadius, maxRadius).toDouble()
        var angleUp = 0f
        while (angleUp < MathUtils.PI / 3) {
            // The more up we go, the less cubes we place, otherwise they'll overlap.
            val piFractions = (1 - Math.abs(MathUtils.sin(angleUp - MathUtils.PI / minRadius / 2))) * 30
            val sphereY = radiusUp * MathUtils.sin(angleUp)
            var i = 0f
            while (i < 2 * Math.PI) {
                val ringRadius = radiusUp * MathUtils.cos(angleUp)
                val x = ringRadius * MathUtils.cos(i)
                val z = ringRadius * MathUtils.sin(i)
                addBlock(x.toFloat(), sphereY.toFloat(), z.toFloat(), false, false, false, false)
                i += MathUtils.PI / piFractions
            }
            angleUp += MathUtils.PI / maxRadius
        }
    }

    protected fun addTrees(minRadius: Float = 50f, maxRadius: Float = 70f) {
        var rad = 0f
        while (rad < MathUtils.PI2 * 12) {
            val r = MathUtils.random(minRadius, maxRadius)
            val x = Math.cos(rad.toDouble()) * r
            val z = Math.sin(rad.toDouble()) * r
            val v = Vector3(x.toFloat(), MathUtils.random(-30f, 15f), z.toFloat())
            addModel<TreeEntity>(v)
            addBlock(v.x, v.y, v.z, false, false, false, false)
            rad += MathUtils.PI / MathUtils.random(8f, 16f)
        }
    }

    protected fun addBlock(x: Float, y: Float, z: Float, addWithPhysics: Boolean, animate: Boolean, rotate: Boolean, drawOnMap: Boolean = true) {
        if (addWithPhysics) {
            val b1 = PhysicalBlockEntity()
            b1.animate = animate
            b1.rotate = rotate
            b1.transform.setTranslation(Vector3(x, y, z))
            b1.mapInfo.draw = drawOnMap
            add(b1)
        } else {
            val b2 = BlockEntity()
            b2.transform.setTranslation(Vector3(x, y, z))
            b2.mapInfo.draw = drawOnMap
            add(b2)
        }
    }

    fun add(entity: AbstractEntity) {
        entities.add(entity)
        entityManager.add(entity)
    }

    val mapData: MapData
        get() {
            val m = entityManager
                    .filter { e -> (e is IMappable) && e.mapInfo.draw }
                    .sortedBy { e -> e.translation.y } // Draw top most entity last.
                    .map { e ->
                        Pair(Vector2(e.translation.x, e.translation.z), (e as IMappable).mapInfo.copy())
                    }

            val minX = m.sortedBy { e -> e.first.x }.first().first.x
            val maxX = m.sortedByDescending { e -> e.first.x }.first().first.x
            val minY = m.sortedBy { e -> e.first.y }.first().first.y
            val maxY = m.sortedByDescending { e -> e.first.y }.first().first.y
            val sizeX = Math.abs(minX) + Math.abs(maxX)
            val sizeY = Math.abs(minY) + Math.abs(maxY)

            val tileSize = 8
            val pixelMap = Pixmap(sizeX.toInt() * tileSize + tileSize / 2 * tileSize, sizeY.toInt() * tileSize + tileSize / 2 * tileSize, Pixmap.Format.RGBA8888)

            pixelMap.blending = Pixmap.Blending.None

            pixelMap.setColor(Color(0f, 0f, 0f, .75f))
            pixelMap.fillRectangle(0, 0, pixelMap.width, pixelMap.height)

            pixelMap.setColor(Color(0f, 0f, 0f, .6f))
            pixelMap.fillRectangle(4, 4, pixelMap.width - tileSize, pixelMap.height - tileSize)

            m.forEach { e ->
                val y = MathUtils.ceil(e.first.y) + Math.abs(minY).toInt()
                val x = MathUtils.floor(e.first.x) + Math.abs(minX).toInt()
                pixelMap.setColor(e.second.color)
                pixelMap.fillRectangle(x * tileSize + tileSize, y * tileSize + tileSize, tileSize * 2 - 3, tileSize * 2 - 3)
            }

            return MapData(m, Texture(pixelMap))
        }

    inline fun <reified T : AbstractEntity> addModel(v: Vector3, scale: Float = 1f) {
        add(T::class.constructors.first().call().apply {
            transform.scl(scale)
            transform.setTranslation(v)
        })
    }
}