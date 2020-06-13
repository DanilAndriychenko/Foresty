package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public enum AnimalsTypes {


    RABBIT(fillAnimationHashMap("PC Computer - Stardew Valley - Dog Blonde.png",
            32, 32, 4, 4, 6, false), 4, 3),
    HORSE(fillAnimationHashMap("PC Computer - Stardew Valley - Horse.png",
            32, 32, 7, 4, 3, true), 3, 4),
    SHEEP(fillAnimationHashMap("PC Computer - Stardew Valley - Sheep.png",
            32, 32, 4, 4, 4, true), 2, 2),
    GOAT(fillAnimationHashMap("PC Computer - Stardew Valley - Goat.png",
            32, 32, 4, 4, 4, true), 2, 2),
    GOAT_BABY(fillAnimationHashMap("PC Computer - Stardew Valley - Goat Baby.png",
            32, 32, 4, 4, 4, true), 2, 2);

    private HashMap<String, Animation<TextureRegion>> stringAnimationHashMap;
    private int animalXVel, animalYVel;
    private static final float FRAME_DURATION = 0.025f;

    AnimalsTypes(HashMap<String, Animation<TextureRegion>> stringAnimationHashMap, int animalXVel, int animalYVel) {
        this.stringAnimationHashMap = stringAnimationHashMap;
        this.animalXVel = animalXVel;
        this.animalYVel = animalYVel;
    }

    public HashMap<String, Animation<TextureRegion>> getStringAnimationHashMap() {
        return stringAnimationHashMap;
    }

    public int getAnimalXVel() {
        return animalXVel;
    }

    public int getAnimalYVel() {
        return animalYVel;
    }

    private static HashMap<String, Animation<TextureRegion>> fillAnimationHashMap(String texturePath, int tileWidth, int tileHeight, int spritesInMoveRow,
                                                                                  int spritesInStandRow, int rowNumStandSprite, boolean secRowAsFourth) {
        HashMap<String, Animation<TextureRegion>> stringAnimationHashMap = new HashMap<>();
        Texture texture = new Texture(Gdx.files.internal(texturePath));
        TextureRegion[][] textureRegion = TextureRegion.split(texture, tileWidth, tileHeight);
        TextureRegion[] south = new TextureRegion[spritesInMoveRow];
        TextureRegion[] east = new TextureRegion[spritesInMoveRow];
        TextureRegion[] north = new TextureRegion[spritesInMoveRow];
        TextureRegion[] west = new TextureRegion[spritesInMoveRow];
        TextureRegion[] stand = new TextureRegion[spritesInStandRow];
        for (int i = 0; i < spritesInMoveRow; i++) {
            if (secRowAsFourth && i == 3) {
                for (int j = 0; j < spritesInMoveRow; j++) west[j] = new TextureRegion(east[j]);
            }
            for (int j = 0; j < spritesInMoveRow; j++) {
                if (i == 0) south[j] = textureRegion[i][j];
                else if (i == 1) east[j] = textureRegion[i][j];
                else if (i == 2) north[j] = textureRegion[i][j];
                else if (i == 3) {
                    if (secRowAsFourth) west[j].flip(true, false);
                    else west[j] = textureRegion[i][j];
                }
            }
        }
        for (int j = 0; j < spritesInStandRow; j++) stand[j] = textureRegion[rowNumStandSprite][j];
        //TODO: reduce frame animation time.
        Animation moveSouth = new Animation<>(FRAME_DURATION, south);
        Animation moveEast = new Animation<>(FRAME_DURATION, east);
        Animation moveNorth = new Animation<>(FRAME_DURATION, north);
        Animation moveWest = new Animation<>(FRAME_DURATION, west);
        Animation standAnimation = new Animation<>(FRAME_DURATION, stand);
        stringAnimationHashMap.put("south", moveSouth);
        stringAnimationHashMap.put("east", moveEast);
        stringAnimationHashMap.put("north", moveNorth);
        stringAnimationHashMap.put("west", moveWest);
        stringAnimationHashMap.put("stand", standAnimation);
        return stringAnimationHashMap;
    }


}
