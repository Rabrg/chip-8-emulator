package me.rabrg.chip8.hardware;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public final class Display {

    /**
     * The background color.
     */
    private static final Color BACKGROUND = new Color(0.13f, 0.55f, 0.55f, 0f);

    /**
     * The foreground color.
     */
    private static final Color FOREGROUND = new Color(0.42f, 0.81f, 0.80f, 0f);

    /**
     * The width of the display in pixels.
     */
    public static int WIDTH = 64;

    /**
     * The height of the display in pixels.
     */
    public static int HEIGHT = 32;

    /**
     * The pixel values of the display.
     */
    private final int[][] values = new int[HEIGHT][WIDTH];

    /**
     * The shape renderer for drawing the rectangular pixels.
     */
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    /**
     * The camera for inverting the y-axis.
     */
    private final OrthographicCamera camera = new OrthographicCamera();

    /**
     * Whether or not the display gets updated the next cycle.
     */
    public boolean render;
    /**
     * The scale of the display.
     */
    private int scale;

    /**
     * Renders the display if the render flag is true.
     */
    public void render() {
        if (render) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (int y = 0; y < values.length; y++) {
                for (int x = 0; x < values[y].length; x++) {
                    shapeRenderer.setColor(values[y][x] > 0 ? FOREGROUND : BACKGROUND);
                    shapeRenderer.rect(x * scale, y * scale, scale, scale);
                }
            }
            shapeRenderer.end();
        }
    }

    /**
     * Draws the specified sprite at the specified coordinates.
     *
     * @param x      The x coordinate.
     * @param y      The y coordinate.
     * @param sprite The sprite.
     * @return Whether or not a pixel was erased.
     */
    public boolean draw(final int x, final int y, final int[] sprite) {
        boolean erased = false;
        for (int i = 0; i < sprite.length; i++) {
            for (int j = 7; j >= 0; j--) {
                if (((sprite[i] >> j) & 1) == 1) {
                    int indexY = (y + i) % HEIGHT;
                    int indexX = (x + 8 - j) % WIDTH;
                    values[indexY][indexX] ^= 1;
                    if (values[indexY][indexX] == 0)
                        erased = true;
                }
            }
        }
        return erased;
    }

    /**
     * Clears the display setting all pixels to zero.
     */
    public void clear() {
        for (int y = 0; y < HEIGHT; y++)
            for (int x = 0; x < WIDTH; x++)
                values[y][x] = 0;
    }

    /**
     * Resizes the display to the specified width and height.
     *
     * @param width  The width.
     * @param height The height.
     */
    public void resize(final int width, final int height) {
        scale = Math.min(width / WIDTH, height / HEIGHT);
        camera.setToOrtho(true, width, height);
        camera.update();
    }
}
