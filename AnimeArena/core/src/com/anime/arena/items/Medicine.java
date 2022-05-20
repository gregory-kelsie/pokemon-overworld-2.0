package com.anime.arena.items;

import com.anime.arena.objects.Player;
import com.anime.arena.pokemon.BasePokemonFactory;
import com.anime.arena.pokemon.Pokemon;
import com.anime.arena.pokemon.PokemonUtils;
import com.anime.arena.pokemon.StatusCondition;

public class Medicine extends Item {
//Berry can extend Medicine since it has a Usable part plus the pinch part.
    private int healAmount;
    private StatusCondition healStatus;
    private boolean revive;
    private boolean fullyHeal;
    private boolean healAnyStatus;

    public Medicine(int itemID, String name, String description, String itemImage, int itemType) {
        super(itemID, name, description, itemImage, itemType);
        healAmount = 0;
        healStatus = null;
        revive = false;
        fullyHeal = false;
        healAnyStatus = false;
    }

    public int getHealAmount() {
        return healAmount;
    }

    public void setHealAmount(int healAmount) {
        this.healAmount = healAmount;
    }

    public StatusCondition getHealStatus() {
        return healStatus;
    }

    public void setHealStatus(StatusCondition healStatus) {
        this.healStatus = healStatus;
    }

    public boolean isRevive() {
        return revive;
    }

    public void setRevive(int revive) {
        if (revive == 1) {
            this.revive = true;
        } else {
            this.revive = false;
        }
    }

    public boolean isFullyHeal() {
        return fullyHeal;
    }

    public void setFullyHeal(int fullyHeal) {
        if (fullyHeal == 1) {
            this.fullyHeal = true;
        } else {
            this.fullyHeal = false;
        }
    }

    public boolean isHealAnyStatus() {
        return healAnyStatus;
    }

    public void setHealAnyStatus(int healAnyStatus) {
        if (healAnyStatus == 1) {
            this.healAnyStatus = true;
        } else {
            this.healAnyStatus = false;
        }
    }

    @Override
    public boolean use(Player player, Pokemon p, BasePokemonFactory factory) {
        boolean usedItem = false;
        if (p.getCurrentHealth() == 0 && revive) {
            PokemonUtils.setCurrentHealthAfterRevive(p);
            usedItem = true;
        } else if (p.getCurrentHealth() == 0) {
            return false;
        } else if (p.getCurrentHealth() > 0 && revive) {
            return false;
        }
        if (healAmount > 0 && p.getCurrentHealth() < p.getHealthStat()) {
            PokemonUtils.healPokemon(p, healAmount);
            usedItem = true;
        }
        if (!PokemonUtils.isStatusFree(p)
                && (healAnyStatus || (healStatus != null && p.getUniqueVariables().getStatus() == healStatus))) {
            PokemonUtils.recoverStatus(p);
            usedItem = true;
        }
        if (fullyHeal && !PokemonUtils.isFullHealth(p)) {
            PokemonUtils.maxHealPokemon(p);
            usedItem = true;
        }
        return usedItem;
    }

    public boolean isUsable() {
        return true;
    }
}
