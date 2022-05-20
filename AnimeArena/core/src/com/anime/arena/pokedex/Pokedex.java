package com.anime.arena.pokedex;

import com.anime.arena.pokemon.BasePokemonFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Pokedex {

    private HashMap<Integer, SeenObtained> pokedex;
    public Pokedex(BasePokemonFactory factory) {
        pokedex = new HashMap<Integer, SeenObtained>();
        for (Integer i : factory.getDatabase().keySet()) {
            pokedex.put(i, new SeenObtained());
        }
    }

    public int getPreviousDexNum(int currentDexNum) {
        int startingDexNum = currentDexNum - 1;
        if (startingDexNum == -1) {
            return -1;
        }
        for (int i = startingDexNum; i > 0; i--) {
            if (hasSeen(i)) {
                return i;
            }
        }
        return -1;
    }

    public int getNextDexNum(int currentDexNum) {
        int startingDexNum = currentDexNum + 1;
        if (startingDexNum == 888) {
            return -1;
        }
        for (int i = startingDexNum; i < 888; i++) {
            if (hasSeen(i)) {
                return i;
            }
        }
        return -1;
    }

    public int getFirstSeenPokemon() {
        for (int i = 1; i < 887; i++) {
            if (hasSeen(i)) {
                return i;
            }
        }
        return -1;
    }


    public boolean hasSeen(int dexNum) {
        if (pokedex.get(dexNum).hasObtained() || pokedex.get(dexNum).hasSeen()) {
            return true;
        }
        return false;
    }

    public boolean hasObtained(int dexNum) {
        if (pokedex.get(dexNum).hasObtained()) {
            return true;
        }
        return false;
    }

    public void updateSeen(int id) {
        pokedex.get(id).setSeen(true);
    }

    public void updateObtained(int id) {
        pokedex.get(id).setSeen(true);
        pokedex.get(id).setObtained(true);
    }

    public List<Integer> getPokedexAmounts() {
        int seenCount = 0;
        int obtainedCount = 0;
        for (SeenObtained seenObtained : pokedex.values()) {
            if (seenObtained.hasSeen()) {
                seenCount++;
            }
            if (seenObtained.hasObtained()) {
                obtainedCount++;
            }
        }
        List<Integer> seenObtainedPair = new ArrayList<Integer>();
        seenObtainedPair.add(new Integer(seenCount));
        seenObtainedPair.add(new Integer(obtainedCount));
        return seenObtainedPair;
    }
}
