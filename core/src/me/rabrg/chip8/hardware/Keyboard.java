package me.rabrg.chip8.hardware;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

// TODO: Mobile device input
public final class Keyboard extends InputAdapter {

    /**
     * The state of all keys.
     */
    private final boolean[] keys = new boolean[0x10];

    /**
     * Whether or not a key has been pressed.
     */
    private boolean pressed;

    @Override
    public boolean keyDown(final int keycode) {
        final int hex = getHex(keycode);
        if (hex != -1) {
            keys[hex] = true;
            pressed = true;
        }
        return true;
    }

    @Override
    public boolean keyUp(final int keycode) {
        final int hex = getHex(keycode);
        if (hex != -1)
            keys[hex] = false;
        return true;
    }

    /**
     * Gets the hex value of the specified libgdx keycode.
     *
     * @param keycode The libgdx keycode.
     * @return The hex value.
     */
    private int getHex(final int keycode) {
        if (keycode == Input.Keys.NUM_1)
            return 0x0;
        if (keycode == Input.Keys.NUM_2)
            return 0x1;
        if (keycode == Input.Keys.NUM_3)
            return 0x2;
        if (keycode == Input.Keys.NUM_4)
            return 0x3;
        if (keycode == Input.Keys.Q)
            return 0x4;
        if (keycode == Input.Keys.W)
            return 0x5;
        if (keycode == Input.Keys.E)
            return 0x6;
        if (keycode == Input.Keys.R)
            return 0x7;
        if (keycode == Input.Keys.A)
            return 0x8;
        if (keycode == Input.Keys.S)
            return 0x9;
        if (keycode == Input.Keys.D)
            return 0xA;
        if (keycode == Input.Keys.F)
            return 0xB;
        if (keycode == Input.Keys.Z)
            return 0xC;
        if (keycode == Input.Keys.X)
            return 0xD;
        if (keycode == Input.Keys.C)
            return 0xE;
        if (keycode == Input.Keys.V)
            return 0xF;
        return -1;
    }

    /**
     * Returns the state of the specified key.
     *
     * @param hex The key.
     * @return The state of the key.
     */
    public boolean isKeyPressed(final int hex) {
        return keys[hex];
    }

    /**
     * Resets the pressed flag.
     */
    public void depress() {
        pressed = false;
    }

    /**
     * Returns whether or not a key has been pressed since the last depression.
     *
     * @return Whether or not a key has been pressed.
     */
    public boolean isPressed() {
        return pressed;
    }
}
