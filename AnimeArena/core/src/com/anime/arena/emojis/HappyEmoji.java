package com.anime.arena.emojis;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class HappyEmoji extends Emoji {
    public HappyEmoji(TextureAtlas emojis) {
        super(emojis, false);
        initHappyEmoji();
    }

    private void initHappyEmoji() {
        Array<TextureRegion> frames = new Array<TextureRegion>();
        frames.add(emojis.findRegion("emoji-happy1"));
        frames.add(emojis.findRegion("emoji-happy2"));
        dotsEmoji = new Animation<TextureRegion>(0.1f, frames);
    }
}
