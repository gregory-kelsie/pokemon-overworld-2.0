package com.anime.arena.emojis;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class SadEmoji extends Emoji {
    public SadEmoji(TextureAtlas emojis) {
        super(emojis, false);
        initSadEmoji();
    }

    private void initSadEmoji() {
        Array<TextureRegion> frames = new Array<TextureRegion>();
        frames.add(emojis.findRegion("emoji-sad1"));
        frames.add(emojis.findRegion("emoji-sad2"));
        dotsEmoji = new Animation<TextureRegion>(0.1f, frames);
    }
}
