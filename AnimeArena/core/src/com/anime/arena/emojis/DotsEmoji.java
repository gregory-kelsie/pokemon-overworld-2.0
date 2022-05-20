package com.anime.arena.emojis;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class DotsEmoji extends Emoji {
    public DotsEmoji(TextureAtlas emojis) {
        super(emojis, true);
        initDotsEmoji();
    }

    private void initDotsEmoji() {
        Array<TextureRegion> frames = new Array<TextureRegion>();
        frames.add(emojis.findRegion("emoji-dots1"));
        frames.add(emojis.findRegion("emoji-dots2"));
        dotsEmoji = new Animation<TextureRegion>(0.5f, frames);
    }
}
