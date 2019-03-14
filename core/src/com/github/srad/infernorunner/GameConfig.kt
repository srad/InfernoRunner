package com.github.srad.infernorunner

import com.badlogic.gdx.Files
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import java.awt.Toolkit

object GameConfig : LwjglApplicationConfiguration() {
    const val rebuildAtlas = false
    const val debug = true
    const val buttonAtlas = "packed/ui/buttons.atlas"

    private val screenSize = Toolkit.getDefaultToolkit().screenSize

    init {
        width = (screenSize.width * 0.9f).toInt()
        height = (screenSize.height * 0.9f).toInt()
        title = "InfernoRunner"
        resizable = false
        fullscreen = false
        samples = 4
        vSyncEnabled = true
        addIcon("image/icon.png", Files.FileType.Internal)
    }
}