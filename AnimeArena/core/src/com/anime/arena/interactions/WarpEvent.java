package com.anime.arena.interactions;

import com.anime.arena.objects.WarpObject;
import com.anime.arena.screens.PlayScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WarpEvent extends Event {

    private String mapName;
    private int warpX;
    private int warpY;
    private int warpStatus; //0 = not started, 1 = fading out 2 = fading in
    public WarpEvent(PlayScreen screen, String mapName, int warpX, int warpY) {
        super(screen);
        this.mapName = mapName;
        this.warpX = warpX;
        this.warpY = warpY;
        this.warpStatus = 0;
    }

    @Override
    public void update(float dt) {
        if (warpStatus == 0) {
            screen.startFadeOutAnimation();

            warpStatus = 1;
        }
        if (warpStatus == 1 && screen.getFadeOutAnimation().isFinished()) {
            Gdx.app.log("before load", dt + "");
            screen.loadMap(mapName);
            warpStatus = 2;
        } else if (warpStatus == 4 && screen.getFadeInAnimation().isFinished()) {
            screen.stopFadeInAnimation();
            screen.triggerMapHeader();
            screen.setEvent(nextEvent);
        } else if (warpStatus == 2) {
            //Second Status allows the game loop to finish loading the map so that there isn't a large dt being sent to the fade animation
            screen.getPlayer().setYTile(warpY);
            screen.getPlayer().setXTile(warpX);
            screen.getPlayer().clampX();
            screen.getPlayer().clampY();
            screen.adjustOffscreenCamera();
            screen.removeMapHUDAnimations();
            warpStatus = 3;
        } else if (warpStatus == 3) {
            //Now that the second status is complete the game loop should have regular dt intervals since the map loading should be done. Start fading in.
            screen.stopFadeOutAnimation();
            screen.startFadeInAnimation();
            warpStatus = 4;
        }
    }

    @Override
    public void interact() {

    }

    @Override
    public boolean isFinishedEvent() {
        return false;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void clickedUp() {

    }

    @Override
    public void clickedDown() {

    }

    @Override
    public void clickedLeft() {

    }

    @Override
    public void clickedRight() {

    }

    @Override
    public void render(SpriteBatch batch) {

    }
}
