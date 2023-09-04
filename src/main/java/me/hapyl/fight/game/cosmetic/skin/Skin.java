package me.hapyl.fight.game.cosmetic.skin;

import me.hapyl.fight.game.heroes.Equipment;
import me.hapyl.fight.game.heroes.Heroes;
import org.bukkit.entity.Player;

public abstract class Skin implements EffectHandler {

    private final Heroes hero;
    private final String name;
    private final Equipment equipment;

    private String description;

    public Skin(Heroes hero, String name) {
        this.hero = hero;
        this.name = name;
        this.equipment = new Equipment();
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public Heroes getHero() {
        return hero;
    }

    public void equip(Player player) {
        getEquipment().equip(player);
    }

}
