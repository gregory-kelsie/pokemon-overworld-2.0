package com.anime.arena.animation;

import com.anime.arena.tools.TextFormater;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class MapHeader {

    private float elapsedTime;
    private int mapNameX;
    private int mapNameState;
    private Texture mapNameBoxTexture;
    private BitmapFont regularFont;

    private String mapHeaderName;
    public MapHeader() {
        mapNameX = 1080;
        elapsedTime = 0;
        mapNameState = 0;
        mapNameBoxTexture = new Texture("hud/maptitleborder.png");
        regularFont = new BitmapFont(Gdx.files.internal("hud/regularFont.fnt"));
        regularFont.setColor(Color.BLACK);
        mapHeaderName = "";
    }

    public void setMapHeaderName(String name) {
        this.mapHeaderName = name;
    }
    
    public void resetMapBox() {
        mapNameX = 1080;
        mapNameState = 0;
    }

    public boolean isFinishedAnimation() {
        if (mapNameState == 2 && mapNameX >= 1080) {
            resetMapBox();
            return true;
        }
        return false;
    }


    public void update(float dt) {
        elapsedTime += dt;
        if (mapNameState == 0) {
            if (elapsedTime >= 0.016) {
                mapNameX -= 1200 * dt;
                if (mapNameX <= 600) {
                    mapNameState = 1;
                }
                elapsedTime = 0;
            }
        } else if (mapNameState == 2){
            if (elapsedTime >= 0.016) {
                mapNameX += 1200 * dt;
            }
        } else if (mapNameState == 1) {
            if (elapsedTime >= 1.5) {
                elapsedTime = 0;
                mapNameState = 2;
            }
        }
    }

    public void dispose() {
        regularFont.dispose();
        mapNameBoxTexture.dispose();
    }

    public void render(Batch batch) {
        batch.draw(mapNameBoxTexture, mapNameX, 1800, 464, 88);
        regularFont.draw(batch, mapHeaderName, mapNameX + 232 - ((int) TextFormater.getWordLengthValue(mapHeaderName) * 12), 1862);
    }
}
