package com.anime.arena.emojis;

import com.badlogic.gdx.graphics.g2d.*;

public class EmojiHandler {

    private Emoji currentEmoji;
    private float emojiTimer;
    private Emoji dotsEmoji;
    private Emoji happyEmoji;
    private Emoji sadEmoji;
    private Emoji noEmoji;
    private Emoji exclamationEmoji;
    private TextureAtlas emojis;


    public EmojiHandler(TextureAtlas emojiAtlas) {
        emojiTimer = 0;
        emojis = emojiAtlas;
        dotsEmoji = new DotsEmoji(emojis);
        happyEmoji = new HappyEmoji(emojis);
        sadEmoji = new SadEmoji(emojis);
        noEmoji = new NoEmoji(emojis);
        exclamationEmoji = new ExclamationEmoji(emojis);
    }


    public void emoteDots() {
        setEmoji(dotsEmoji);
    }

    public void emoteHappy() {
        setEmoji(happyEmoji);
    }

    public void emoteExclamation() {
        setEmoji(exclamationEmoji);
    }

    public void emoteSad() {
        setEmoji(sadEmoji);
    }

    public void emoteNo() {
        setEmoji(noEmoji);
    }

    private void setEmoji(Emoji newEmoji) {
        if (currentEmoji == null) {
            currentEmoji = newEmoji;
            emojiTimer = 0;
        }
    }

    public void resetEmojis() {
        currentEmoji = null;
        emojiTimer = 0;
    }

    public void update(float dt) {
        if (currentEmoji != null) {
            emojiTimer += dt;
            if (emojiTimer >= 3) {
                resetEmojis();
            }
        }
    }

    public void draw(Batch batch, float x, float y) {
        if (currentEmoji != null) {
            currentEmoji.draw(batch, emojiTimer, x, y);
        }
    }
}
