package com.github.srad.infernorunner.core

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.utils.Disposable
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

class Resource<out T : Disposable>(private val file: String, private val type: KClass<out T>) : Disposable {
    override fun dispose() {
        Asset.manager.unload(file)
    }

    val load: T by lazy {
        Asset.manager.load(file, type.java)
        Asset.manager.finishLoadingAsset(file)
        Asset.manager.get<T>(file)
    }

    private object Asset {
        val manager = AssetManager()
    }

    companion object : Disposable {
        override fun dispose() {
            Asset.manager.dispose()
        }

        val buttonAtlas = Resource(com.github.srad.infernorunner.GameConfig.buttonAtlas, TextureAtlas::class)
        val creepyMusic = Resource("audio/creepy.mp3", Music::class)
        val theme1 = Resource("audio/music_theme_1.mp3", Music::class)
        val theme2 = Resource("audio/music_theme_2.mp3", Music::class)
        val laserSound = Resource("audio/laser.mp3", Sound::class)
        val hellMusic = Resource("audio/lava.mp3", Music::class)

        val static = Resource("audio/static.mp3", Sound::class)
        val screamSound = Resource("audio/scream-short.mp3", Sound::class)
        val demonSound = Resource("audio/demon.mp3", Sound::class)
        val fresh = Resource("audio/fresh.mp3", Sound::class)
        val jumpSound = Resource("audio/sigh.mp3", Sound::class)
        val pickSound = Resource("audio/poke.mp3", Sound::class)
        val bump = Resource("audio/bump.mp3", Sound::class)
        val doorSound = Resource("audio/door.mp3", Sound::class)
        val key = Resource("audio/key.mp3", Sound::class)
        val comeHere = Resource("audio/come-here.mp3", Sound::class)
        val walking = Resource("audio/walking.mp3", Sound::class)
        val satanBreath = Resource("audio/satan-breathe.mp3", Sound::class)
        val paperSound = Resource("audio/paper.mp3", Sound::class)
        val towerAttack = Resource("audio/towerattack.mp3", Sound::class)

        val block = Resource("models/satan-cube/satan-cube-3.g3db", Model::class)
        val block2 = Resource("models/satan-cube/satan-cube-4.g3db", Model::class)
        val reaperModel = Resource("models/reaper/blade.g3db", Model::class)
        val sphere = Resource("models/sphere/sphere.g3db", Model::class)
        val cross = Resource("models/cross2/cross2.g3db", Model::class)
        val lifeModel = Resource("models/life/life.g3db", Model::class)
        val coffin = Resource("models/coffin/coffin.g3db", Model::class)
        val shieldModel = Resource("models/satan-shield/satan-shield-2.g3db", Model::class)
        val goalModel = Resource("models/goal/goal.g3db", Model::class)
        val shop = Resource("models/shop4/shop.g3db", Model::class)
        val gravestone = Resource("models/gravestone/gravestone.g3db", Model::class)
        val spiderModel = Resource("models/spider/spider.g3db", Model::class)
        val treeModel = Resource("models/tree1/tree1.g3db", Model::class)
        val groundModel = Resource("models/ground/ground.g3db", Model::class)
        val portalModel = Resource("models/portal/portal3.g3db", Model::class)
        val fountainModel = Resource("models/fountain/fountain.g3db", Model::class)
        val towerModel = Resource("models/tower/tower3.g3db", Model::class)
        val projectileModel = Resource("models/projectile/projectile.g3db", Model::class)

        val satanFace = Resource("image/texture/satan-face.png", Texture::class)
        val hudTexture = Resource("image/ui/blood-hud-3.png", Texture::class)
        val bloodTexture = Resource("image/texture/blood1.png", Texture::class)
        val startBackground = Resource("image/texture/bg-blood.png", Texture::class)
        val bloodBackground = Resource("image/texture/bg-blood-plain.png", Texture::class)
        val bloodBackgroundCrossed = Resource("image/texture/bg-blood-plain-crossed.png", Texture::class)
        val startBackgroundBump = Resource("image/texture/bg-blood-bump.png", Texture::class)
        val lifeTexture = Resource("image/ui/life.png", Texture::class)
        val satanEndTexture = Resource("image/texture/satan-end.png", Texture::class)
        val satanEndBumpTexture = Resource("image/texture/satan-end-bump.png", Texture::class)
        val satanOverlay = Resource("image/texture/satan-overlay-4.png", Texture::class)
        val ouija = Resource("image/texture/ouija.png", Texture::class)

        fun exists() {
            Resource.Companion::class.memberProperties.forEach { prop ->
                val file = File((prop.get(Resource.Companion) as Resource<*>).file)
                if (!file.exists()) {
                    throw error("File is missing: ${file.absoluteFile}")
                }
            }
        }
    }
}