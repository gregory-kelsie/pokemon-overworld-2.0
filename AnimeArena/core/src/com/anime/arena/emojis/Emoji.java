package com.anime.arena.emojis;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class Emoji {
    protected TextureAtlas emojis;
    protected Animation<TextureRegion> dotsEmoji;
    protected boolean looping;
    public Emoji(TextureAtlas emojis, boolean looping) {
        this.emojis = emojis;
        this.looping = looping;
    }

    public void draw(Batch batch, float emojiTimer, float x, float y) {
        batch.draw(dotsEmoji.getKeyFrame(emojiTimer, looping), x, y);
    }
}
