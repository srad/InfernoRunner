package com.github.srad.infernorunner.core

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.utils.Disposable
import kotlin.reflect.KClass

/**
 * Container for all asset types. Call property to return the actual asset.
 * As the name implies as of then it's dynamically loaded.
 */
sealed class Resource<out T : Disposable>(private val file: String, private val type: KClass<out T>) : Disposable {
    class TextureResource(file: String) : Resource<Texture>("image/$file", Texture::class)
    class ModelResource(file: String) : Resource<Model>("models/$file", Model::class)
    class MusicResource(file: String) : Resource<Music>("audio/$file", Music::class)
    class SoundResource(file: String) : Resource<Sound>("audio/$file", Sound::class)
    class TextureAtlasResource(file: String) : Resource<TextureAtlas>(file, TextureAtlas::class)

    override fun dispose() {
        Asset.manager.unload(file)
    }

    val load: T by lazy {
        // Not sure why AssetManager doesn't check first,
        // because it does more elaborate things first.
        if (!LoadedFiles.contains(file)) {
            Asset.manager.load(file, type.java)
            Asset.manager.finishLoadingAsset(file)
        }
        Asset.manager.get<T>(file)
    }

    private object Asset {
        val manager = AssetManager()
    }

    companion object : Disposable {
        private val LoadedFiles = HashSet<String>()

        override fun dispose() {
            Asset.manager.dispose()
        }

        val buttonAtlas = TextureAtlasResource(com.github.srad.infernorunner.GameConfig.buttonAtlas)

        val creepyMusic = MusicResource("creepy.mp3")
        val theme1 = MusicResource("music_theme_1.mp3")
        val theme2 = MusicResource("music_theme_2.mp3")
        val hellMusic = MusicResource("lava.mp3")

        val laserSound = SoundResource("laser.mp3")
        val static = SoundResource("static.mp3")
        val screamSound = SoundResource("scream-short.mp3")
        val demonSound = SoundResource("demon.mp3")
        val fresh = SoundResource("fresh.mp3")
        val jumpSound = SoundResource("sigh.mp3")
        val pickSound = SoundResource("poke.mp3")
        val bump = SoundResource("bump.mp3")
        val doorSound = SoundResource("door.mp3")
        val key = SoundResource("key.mp3")
        val comeHere = SoundResource("come-here.mp3")
        val walking = SoundResource("walking.mp3")
        val satanBreath = SoundResource("satan-breathe.mp3")
        val paperSound = SoundResource("paper.mp3")
        val towerAttack = SoundResource("towerattack.mp3")

        val block = ModelResource("satan-cube/satan-cube-3.g3db")
        val block2 = ModelResource("satan-cube/satan-cube-4.g3db")
        val reaperModel = ModelResource("reaper/blade.g3db")
        val sphere = ModelResource("sphere/sphere.g3db")
        val cross = ModelResource("cross2/cross2.g3db")
        val lifeModel = ModelResource("life/life.g3db")
        val coffin = ModelResource("coffin/coffin.g3db")
        val shieldModel = ModelResource("satan-shield/satan-shield-2.g3db")
        val goalModel = ModelResource("goal/goal.g3db")
        val shop = ModelResource("shop4/shop.g3db")
        val gravestone = ModelResource("gravestone/gravestone.g3db")
        val spiderModel = ModelResource("spider/spider.g3db")
        val treeModel = ModelResource("tree1/tree1.g3db")
        val groundModel = ModelResource("ground/ground.g3db")
        val portalModel = ModelResource("portal/portal3.g3db")
        val fountainModel = ModelResource("fountain/fountain.g3db")
        val towerModel = ModelResource("tower/tower3.g3db")
        val projectileModel = ModelResource("projectile/projectile.g3db")

        val satanFace = TextureResource("texture/satan-face.png")
        val hudTexture = TextureResource("ui/blood-hud-2.png")
        val bloodTexture = TextureResource("texture/blood1.png")
        val startBackground = TextureResource("texture/bg-blood.png")
        val bloodBackground = TextureResource("texture/bg-blood-plain.png")
        val bloodBackgroundCrossed = TextureResource("texture/bg-blood-plain-crossed.png")
        val startBackgroundBump = TextureResource("texture/bg-blood-bump.png")
        val lifeTexture = TextureResource("ui/life2.png")
        val satanEndTexture = TextureResource("texture/satan-end.png")
        val satanEndBumpTexture = TextureResource("texture/satan-end-bump.png")
        val satanOverlay = TextureResource("texture/satan-overlay-4.png")
        val ouija = TextureResource("texture/ouija.png")
    }
}