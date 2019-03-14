package com.github.srad.infernorunner.core

import com.badlogic.gdx.Gdx

abstract sealed class PrefType<T>(val typeName: String, val default: T, val label: String) {
    class StringPref(typeName: String, default: String, label: String) : PrefType<String>(typeName, default, label)
    class IntPref(typeName: String, default: Int, label: String) : PrefType<Int>(typeName, default, label)
    class FloatPref(typeName: String, default: Float, label: String) : PrefType<Float>(typeName, default, label)
    class BoolPref(typeName: String, default: Boolean, label: String) : PrefType<Boolean>(typeName, default, label)
    class LongPref(typeName: String, default: Long, label: String) : PrefType<Long>(typeName, default, label)

    companion object {
        // Must be lazy because objects below have be initialized first.
        val statistics by lazy { arrayOf(PrefType.PlayerJumps, PrefType.PlayerDeaths, PrefType.PlayerGameOvers, PrefType.PlayerLives, PrefType.PlayerPurchases, PrefType.PlayerHits) }

        val PlayerName = StringPref("player_name", "Player", "Player Name")
        val PlayerJumps = IntPref("player_jumps", 0, "# Jumps")
        val PlayerDeaths = IntPref("player_deaths", 0, "# Deaths")
        val PlayerGameOvers = IntPref("player_game_overs", 0, "# GameOvers")
        val PlayerLives = IntPref("player_game_lives", 0, "# Lives")
        val PlayerPurchases = IntPref("player_game_purchases", 0, "# of Purchases")
        val PlayerHits = IntPref("player_game_hits", 0, "# of Hits")

        val SoundEnabled = BoolPref("sound_enabled", true, "Sound Enabled")
        val SoundVolume = FloatPref("sound_volume", 1f, "Sound Volume")
        val MusicEnabled = BoolPref("music_enabled", true, "Music Enabled")
        val MusicVolume = FloatPref("music_volume", 1f, "Music Volume")

        //TODO: implement
        val MasterVolume = FloatPref("master_volume", 1f, "Master Volume")
        val MasterEnabled = BoolPref("master_enabled", true, "Master Enabled")
    }
}

/** All store-able types are exhausted here, except of storing a Map<..,..>. */
class GamePref {
    /**
     * Must be lazy, so initialization order is delayed until after the application launch,
     * when the settings are actually used. Otherwise NullPointerException.
     */
    private val preferences by lazy { Gdx.app.getPreferences(com.github.srad.infernorunner.GameConfig.title) }

    fun <T> set(pref: PrefType<T>, t: T) {
        when (t) {
            is String -> preferences.putString(pref.typeName, t)
            is Boolean -> preferences.putBoolean(pref.typeName, t)
            is Float -> preferences.putFloat(pref.typeName, t)
            is Int -> preferences.putInteger(pref.typeName, t)
            is Long -> preferences.putLong(pref.typeName, t)
            else -> error("Invalid preference type")
        }
        preferences.flush()
    }

    /** Ignore compiler warning. Casting is correctly determined already by "default" value type. */
    fun <T> get(pref: PrefType<T>): T = when (pref) {
        is PrefType.StringPref -> preferences.getString(pref.typeName, pref.default)
        is PrefType.BoolPref -> preferences.getBoolean(pref.typeName, pref.default)
        is PrefType.FloatPref -> preferences.getFloat(pref.typeName, pref.default)
        is PrefType.IntPref -> preferences.getInteger(pref.typeName, pref.default)
        is PrefType.LongPref -> preferences.getLong(pref.typeName, pref.default)
    } as T
}