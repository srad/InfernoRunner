package com.github.srad.infernorunner.level

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2

data class MapInfo(val legend: String, val color: Color, var draw: Boolean = true)

interface IMappable {
    val mapInfo: MapInfo
}

class MapData(val vectorWithMapInfo: List<Pair<Vector2, MapInfo>>, val map: Texture)
