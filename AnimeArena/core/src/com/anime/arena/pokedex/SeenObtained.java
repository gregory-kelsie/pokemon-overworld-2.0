package com.anime.arena.pokedex;

public class SeenObtained {
    private boolean seen;
    private boolean obtained;
    public SeenObtained() {
        this.seen = false;
        this.obtained = false;
    }

    public SeenObtained(boolean seen, boolean obtained) {
        this.seen = seen;
        this.obtained = obtained;
    }

    public boolean hasSeen() {
        return seen;
    }

    public boolean hasObtained() {
        return obtained;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public void setObtained(boolean obtained) {
        this.obtained = true;
    }
}
