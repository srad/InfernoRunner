package com.github.srad.infernorunner.core

import com.badlogic.gdx.Gdx

data class PrefType<T>(val typeName: String, val default: T, val label: String) {
    companion object {
        // Must be lazy because objects below have be initialized first.
        val statistics by lazy { arrayOf(PrefType.PlayerJumps, PrefType.PlayerDeaths, PrefType.PlayerGameOvers, PrefType.PlayerLives, PrefType.PlayerPurchases, PrefType.PlayerHits) }

        val PlayerName = PrefType("player_name", "Player", "Player Name")
        val PlayerJumps = PrefType("player_jumps", 0, "# Jumps")
        val PlayerDeaths = PrefType("player_deaths", 0, "# Deaths")
        val PlayerGameOvers = PrefType("player_game_overs", 0, "# GameOvers")
        val PlayerLives = PrefType("player_game_lives", 0, "# Lives")
        val PlayerPurchases = PrefType("player_game_purchases", 0, "# of Purchases")
        val PlayerHits = PrefType("player_game_hits", 0, "# of Hits")

        val SoundEnabled = PrefType("sound_enabled", true, "Sound Enabled")
        val SoundVolume = PrefType("sound_volume", 1f, "Sound Volume")
        val MusicEnabled = PrefType("music_enabled", true, "Music Enabled")
        val MusicVolume = PrefType("music_volume", 1f, "Music Volume")

        //TODO: implement
        val MasterVolume = PrefType("master_volume", 1f, "Master Volume")
        val MasterEnabled = PrefType("master_enabled", true, "Master Enabled")
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
            else -> error("Invalid preference type $pref")
        }
        preferences.flush()
    }

    /** Ignore compiler warning. Casting is correctly determined already by "default" value type. */
    fun <T> get(pref: PrefType<T>): T = when (pref.default) {
        is String -> preferences.getString(pref.typeName, pref.default)
        is Boolean -> preferences.getBoolean(pref.typeName, pref.default)
        is Float -> preferences.getFloat(pref.typeName, pref.default)
        is Int -> preferences.getInteger(pref.typeName, pref.default)
        is Long -> preferences.getLong(pref.typeName, pref.default)
        else -> throw error("Cannot read unknown preference: ${pref.typeName}")
    } as T
}