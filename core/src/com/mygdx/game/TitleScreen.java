package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TitleScreen extends ScreenAdapter {

    private static final int FRAME_COLS = 4, FRAME_ROWS = 1;
    private static final long PRESS_BUTTON_AFTER_TIME = (long) (5 * Math.pow(10, 9)),
            LABEL_BLINKS_AFTER_TIME = (long) (0.5 * Math.pow(10, 9)),
            RABBIT_CROSS_FREQUENCY = (long) (6 * Math.pow(10, 9)),
            RABBIT_Y = 5,
            RABBIT_SPEED = 4;
    private static final float FRAME_DURATION_RABBIT = 0.04f,
            SCREEN_WIDTH = Gdx.graphics.getWidth(),
            SCREEN_HEIGHT = Gdx.graphics.getHeight(),
            BIG_LABEL_X = SCREEN_WIDTH * 0.2f,
            BIG_LABEL_Y = SCREEN_HEIGHT * 0.6f,
            BIG_LABEL_WIDTH = SCREEN_WIDTH * 0.6f,
            BIG_LABEL_HEIGHT = SCREEN_HEIGHT * 0.2f,
            SMALL_LABEL_COEFFICIENT = 0.8f;
    private static final Texture rabbitTexture = new Texture(Gdx.files.internal("rabbit.png")),
            startTexture = new Texture(Gdx.files.internal("start.png")),
            startTextureWithLabel = new Texture(Gdx.files.internal("startWithLabel.png")),
            backgroundTexture = new Texture(Gdx.files.internal("titleScreenBackground.jpg"));
    private static Animation<TextureRegion> rabbitAnimation;
    private static TextureRegion rabbitFrame;
    private static float stateTime;
    private static long timeStart, lastBlinkedTime, rabbitFinishedCrossing;
    private static boolean startLabelIsBig = false;
    private static int rabbitX;
    private final Foresty game;

    public TitleScreen(Foresty game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Define all variables.
        timeStart = System.nanoTime();
        lastBlinkedTime = System.nanoTime();

        // Create rabbit animation.
        TextureRegion[][] tmp = TextureRegion.split(rabbitTexture,
                rabbitTexture.getWidth() / FRAME_COLS,
                rabbitTexture.getHeight() / FRAME_ROWS);
        TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                walkFrames[index++] = tmp[i][j];
            }
        }
        rabbitAnimation = new Animation<>(FRAME_DURATION_RABBIT, walkFrames);
        stateTime = 0f;
    }

    @Override
    public void render(float delta) {
        // If user press space key, then go to the next scene.
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            game.setScreen(game.levelsScreen, game.musicOnTitleAndLevelsScreens);
        }

        // For drawing current rabbit animation frame.
        stateTime += Gdx.graphics.getDeltaTime();
        rabbitFrame = rabbitAnimation.getKeyFrame(stateTime, true);

        game.spriteBatch.begin();

        // Drawing background.
        game.spriteBatch.draw(backgroundTexture, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // If time of showing label of some size is over, then change size.
        if (System.nanoTime() - lastBlinkedTime > LABEL_BLINKS_AFTER_TIME) {
            lastBlinkedTime = System.nanoTime();
            if (startLabelIsBig) startLabelIsBig = false;
            else startLabelIsBig = true;
        }

        // Draw appropriate label depending of boolean startLabelIsBig value
        if (!startLabelIsBig) {
            if (System.nanoTime() - timeStart >= PRESS_BUTTON_AFTER_TIME) game.spriteBatch.draw(startTextureWithLabel,
                    BIG_LABEL_X + (BIG_LABEL_WIDTH * (1 - SMALL_LABEL_COEFFICIENT)) * 0.5f,
                    BIG_LABEL_Y + (BIG_LABEL_HEIGHT * (1 - SMALL_LABEL_COEFFICIENT)) * 0.5f,
                    BIG_LABEL_WIDTH * SMALL_LABEL_COEFFICIENT, BIG_LABEL_HEIGHT * SMALL_LABEL_COEFFICIENT);
            else
                game.spriteBatch.draw(startTexture, BIG_LABEL_X + (BIG_LABEL_WIDTH * (1 - SMALL_LABEL_COEFFICIENT)) * 0.5f,
                        BIG_LABEL_Y + (BIG_LABEL_HEIGHT * (1 - SMALL_LABEL_COEFFICIENT)) * 0.5f,
                        BIG_LABEL_WIDTH * SMALL_LABEL_COEFFICIENT, BIG_LABEL_HEIGHT * SMALL_LABEL_COEFFICIENT);
        } else {
            if (System.nanoTime() - timeStart >= PRESS_BUTTON_AFTER_TIME) game.spriteBatch.draw(startTextureWithLabel,
                    BIG_LABEL_X, BIG_LABEL_Y, BIG_LABEL_WIDTH, BIG_LABEL_HEIGHT);
            else game.spriteBatch.draw(startTexture, BIG_LABEL_X, BIG_LABEL_Y, BIG_LABEL_WIDTH, BIG_LABEL_HEIGHT);
        }

        // If rabbit goes out of the screen, then note the time and set rabbit out of the left window border,
        // Otherwise just move rabbit.
        if (rabbitX >= SCREEN_WIDTH) {
            rabbitFinishedCrossing = System.nanoTime();
            rabbitX = -(int) (SCREEN_WIDTH / 25);
        } else if (rabbitFinishedCrossing == 0) {
            rabbitX += RABBIT_SPEED;
        }

        // If time is noted and is over, then reset timer to zero.
        if (rabbitFinishedCrossing != 0 && System.nanoTime() - rabbitFinishedCrossing >= RABBIT_CROSS_FREQUENCY) {
            rabbitFinishedCrossing = 0;
        }

        // Just draw rabbit without worrying for anything.
        game.spriteBatch.draw(rabbitFrame, rabbitX, RABBIT_Y, SCREEN_WIDTH / 25, SCREEN_HEIGHT / 25);

        game.spriteBatch.end();
    }
}
