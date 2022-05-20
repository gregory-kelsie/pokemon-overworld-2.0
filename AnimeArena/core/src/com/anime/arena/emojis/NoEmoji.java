package com.anime.arena.emojis;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class NoEmoji extends Emoji {
    public NoEmoji(TextureAtlas emojis) {
        super(emojis, true);
        initNoEmoji();
    }

    private void initNoEmoji() {
        Array<TextureRegion> frames = new Array<TextureRegion>();
        frames.add(emojis.findRegion("emoji-no1"));
        frames.add(emojis.findRegion("emoji-no2"));
        dotsEmoji = new Animation<TextureRegion>(0.2f, frames);
    }
}
