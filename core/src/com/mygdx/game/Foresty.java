package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Foresty extends Game {

    public SpriteBatch spriteBatch;
    public LevelsScreen levelsScreen;
    public Music musicOnTitleAndLevelsScreens;
    public Music musicOnGameScreen;

    @Override
    public void create() {
        // Define all public variables.
        spriteBatch = new SpriteBatch();
        musicOnTitleAndLevelsScreens = Gdx.audio.newMusic(Gdx.files.internal("TitleScreenMusic.mp3"));
        musicOnGameScreen = Gdx.audio.newMusic(Gdx.files.internal("StardewValley-StardewValleyFairTheme_(tancpol.net).mp3"));
        musicOnTitleAndLevelsScreens.setLooping(true);
        levelsScreen = new LevelsScreen(this);
        levelsScreen.setSavedInfo();

        // Start playing music for title and levels screens.
        //musicOnTitleAndLevelsScreens.setLooping(true);
        //musicOnTitleAndLevelsScreens.play();

        // Set titleScreen as first screen of the game.
        setScreen(new TitleScreen(this), musicOnTitleAndLevelsScreens);
    }

    public void setScreen(Screen screen, Music musicToPlay) {
        setScreen(screen);
        musicOnGameScreen.pause();
        musicOnTitleAndLevelsScreens.pause();
        musicToPlay.play();
    }
}
