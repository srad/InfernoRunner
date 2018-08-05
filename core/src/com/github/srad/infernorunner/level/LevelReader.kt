package com.github.srad.infernorunner.level

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.environment.PointLight
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.JsonReader
import com.github.srad.infernorunner.core.EntityManager
import com.github.srad.infernorunner.core.ILoggable
import com.github.srad.infernorunner.entity.*

private data class ImportEntity(val type: List<String>, val x: Float, val y: Float, val z: Float)

private class ImportedEntities : ArrayList<ImportEntity>()

class LevelReader(private val filename: String, entityManager: EntityManager, environment: Environment, name: String) : AbstractLevelCreator(entityManager, environment, name), ILoggable {
    override fun implementation() {
        var maxX = 0f
        var maxZ = 0f
        val entities = importJson(filename)
        entities.forEach { entity ->
            val v = Vector3(entity.x, entity.y, entity.z)
            entity.type.forEach { type ->
                when (type) {
                    "light" -> {
                        val light = PointLight()
                        light.set(randomColor(), Vector3(entity.x, entity.y + 2f, entity.z), 50f)
                        environment.add(light)
                    }
                // Player falls through these, it's a trap
                    "block" -> addBlock(entity.x, entity.y, entity.z, false, false, false)
                    "block_animated" -> addBlock(entity.x, entity.y, entity.z, false, true, false)
                // solid
                    "block_physical" -> addBlock(entity.x, entity.y, entity.z, true, false, false)
                    "block_physical_animated" -> addBlock(entity.x, entity.y, entity.z, true, true, false)
                    "life" -> addModel<LifeInstance>(v.add(0f, 3f, 0f))
                    "coffin" -> {
                        val coffinInstance = CoffinInstance()
                        coffinInstance.applyTranslation(Vector3(entity.x, entity.y + 5f, entity.z))
                        coffinInstance.applyYRotation((Math.PI / 2).toFloat())
                        add(coffinInstance)
                    }
                    "shop" -> addModel<ShopInstance>(v.add(0f, 5f, 0f))
                    "goal" -> {
                        addModel<GoalInstance>(v)
                        addModel<ShieldInstance>(v.cpy().sub(0f, 3f, 0f))
                    }
                    "portal" -> addModel<PortalInstance>(Vector3(entity.x, entity.y, entity.z))
                    "fountain" -> addModel<FountainInstance>(v.add(0f, 5f, 0f))
                    "tower" -> addModel<TowerInstance>(Vector3(v.add(0f, 25f, 0f)))
                    else -> logError("LevelReader", "unknown-entity: $type")
                }
                maxX = Math.max(entity.x, maxX)
                maxZ = Math.max(entity.z, maxZ)
            }
        }

        // Level decoration
        val maxCoordinate = Math.max(maxZ, maxX) + 40f
        //renderSphere(maxCoordinate, maxCoordinate)
        //addTrees(maxCoordinate - 5f, maxCoordinate)
        addModel<SpiderInstance>(Vector3(MathUtils.random(40f, maxCoordinate), 1f, MathUtils.random(40f, maxCoordinate)))
        addModel<GroundInstance>(Vector3(0f, -33f, 0f), 35f)
        addModel<CrossInstance>(Vector3(0f, 60f, 0f))
        addModel<SphereInstance>(Vector3(), 55f)

        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.6f, 0.2f, .6f))
        environment.add(DirectionalLight().set(0.8f, 0.5f, 0.2f, -2f, -3f, -2f))
    }

    private fun importJson(filename: String): ImportedEntities {
        val import = ImportedEntities()

        try {
            val text = Gdx.files.internal(filename).readString()
            val result = JsonReader().parse(text)
            for (e in result) {
                val types = ArrayList<String>()
                for (i in 0 until e.get("type").count()) {
                    types.add(e.get("type").getString(i))
                }
                // scale to grid correctly
                // x * -1 because x coordinates are mirrored between here and the level-editor
                import.add(ImportEntity(types, e.getFloat("x") * 2, e.getFloat("y") * 2, e.getFloat("z") * 2 * -1))
            }
        } catch (e: Exception) {
            if (e.message != null) {
                logError(e.message!!)
            }
        }

        return import
    }
}