package com.anime.arena.emojis;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class ExclamationEmoji extends Emoji {
    public ExclamationEmoji(TextureAtlas emojis) {
        super(emojis, true);
        initNoEmoji();
    }

    private void initNoEmoji() {
        Array<TextureRegion> frames = new Array<TextureRegion>();
        frames.add(emojis.findRegion("emoji-exclam1"));
        frames.add(emojis.findRegion("emoji-exclam2"));
        dotsEmoji = new Animation<TextureRegion>(0.3f, frames);
    }
}
