package com.github.srad.infernorunner.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.github.srad.infernorunner.GameConfig;
import com.github.srad.infernorunner.InfernoRunner;

public class DesktopLauncher {
    public static void main(String[] arg) {
        new LwjglApplication(new InfernoRunner(), GameConfig.INSTANCE);
    }
}
