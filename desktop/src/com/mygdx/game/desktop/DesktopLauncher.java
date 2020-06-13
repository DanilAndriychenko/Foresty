package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.Foresty;

public class DesktopLauncher {

    public static void main(String[] arg) {

        // Creating config for Foresty game.
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.height = 720;
        config.width = 960;

        //Launch game.
        new LwjglApplication(new Foresty(), config);
    }
}
