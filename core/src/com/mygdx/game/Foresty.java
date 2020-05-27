package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class Foresty extends Game {

    public LevelsScreen levelsScreen;
    public Music music;

    @Override
    public void create() {
        levelsScreen = new LevelsScreen(this);
        setScreen(new TitleScreen(this));
    }
}
