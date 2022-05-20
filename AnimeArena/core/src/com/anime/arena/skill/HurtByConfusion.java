package com.anime.arena.skill;

import com.anime.arena.field.Field;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.pokemon.PokemonType;

import java.util.List;

public class HurtByConfusion extends DamageSkill {
    private static String nm = "Hurt By Confusion";
    private static String desc = "";
    private static SkillCategory cat = SkillCategory.PHYSICAL;
    private static int p = -1;
    private static int acc = 100;
    public HurtByConfusion() {

        super(nm, desc, cat, p, -1, acc, PokemonType.NORMAL, SkillTarget.SELF, 0, 1,
                40, -1);
    }

    /**
     * Damage the enemy, hit self with confusion.
     * @param skillUser The Pokemon using the skill
     * @param enemyPokemon The enemy receiving the skill
     * @param field The field of the battle.
     * @return The results of using the move.
     */
    public void use(BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field) {
        //Use the damage part of the move.
        int dmg = getDamage(skillUser, skillUser, field, null, null, false);
        skillUser.subtractHealth(dmg);
    }
}
