package me.rabrg.chip8.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import me.rabrg.chip8.CHIP8Emulator;

// TODO: mobile device support
public final class DesktopLauncher {

    public static void main(final String[] args) {
        final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1280;
        config.height = 720;
        config.backgroundFPS = 0;
        config.foregroundFPS = 0;
        config.vSyncEnabled = false;
        new LwjglApplication(new CHIP8Emulator(), config);
    }
}
