package me.rabrg.chip8;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import me.rabrg.chip8.hardware.Display;
import me.rabrg.chip8.hardware.Keyboard;
import me.rabrg.chip8.hardware.Processor;

public final class CHIP8Emulator extends ApplicationAdapter {

    /**
     * The processor.
     */
    private Processor processor;

    /**
     * The display.
     */
    private Display display;

    /**
     * The keyboard.
     */
    private Keyboard keyboard;

    /**
     * The time the cycle started in milliseconds.
     */
    private long cycleStart = System.currentTimeMillis();

    @Override
    public void create() {
        processor = new Processor(this);
        display = new Display();
        keyboard = new Keyboard();

        processor.loadROM("BREAKOUT");

        Gdx.input.setInputProcessor(keyboard);
    }

    @Override
    public void render() {
        processor.execute();
        display.render();
        sleep(Processor.CYCLE_RATE);
    }

    @Override
    public void resize(final int width, final int height) {
        display.resize(width, height);
    }

    /**
     * Sleeps if necessary to match the specified cycle rate.
     *
     * @param cycleRate The cycle rate.
     */
    private void sleep(final int cycleRate) {
        long cycleDifference = System.currentTimeMillis() - cycleStart; // TODO: nanosecond precision
        if (cycleDifference < cycleRate) {
            try {
                Thread.sleep(cycleRate - cycleDifference);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
        cycleStart = System.currentTimeMillis();
    }

    /**
     * Gets the keyboard.
     *
     * @return The keyboard.
     */
    public Keyboard getKeyboard() {
        return keyboard;
    }

    /**
     * Gets the display.
     *
     * @return The display.
     */
    public Display getDisplay() {
        return display;
    }
}
