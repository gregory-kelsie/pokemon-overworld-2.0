package com.anime.arena.objects;

import com.anime.arena.screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class BerryFactory {
    public enum BerryType { ORAN(0), SITRUS(1), PERSIM(2), LEPPA(3), ASPEAR(4), RAWST(5), PECHA(6),
        CHESTO(7), CHERI(8), LUM(9);

        private final int value;
        private BerryType(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    };

    private PlayScreen screen;

    public BerryFactory(PlayScreen screen) {
        this.screen = screen;
    }

    public BerryObject getBerryTree(int treeId, int berryType, int x, int y) {
        if (berryType == BerryType.ORAN.getValue()) {
            return new OranTree(treeId, x, y, screen);
        } else {
            return new OranTree(treeId, x, y, screen);
        }
    }
}