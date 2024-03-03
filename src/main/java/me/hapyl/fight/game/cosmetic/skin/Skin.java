package me.hapyl.fight.game.cosmetic.skin;

import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.RubyPurchasable;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.util.Described;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public abstract class Skin implements Described, RubyPurchasable {

    private final Heroes hero;
    private final Equipment equipment;
    private Rarity rarity;

    private String name;
    private String description;
    private long price;

    public Skin(@Nonnull Heroes hero) {
        this.hero = hero;
        this.equipment = new Equipment();
        this.rarity = Rarity.COMMON;

        this.name = "Unnamed skin.";
        this.description = "No description.";
        this.price = -1;
    }

    @Override
    public long getRubyPrice() {
        return price;
    }

    @Override
    public void setRubyPrice(long price) {
        this.price = price;
    }

    @Nonnull
    public Rarity getRarity() {
        return rarity;
    }

    public void setRarity(@Nonnull Rarity rarity) {
        this.rarity = rarity;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    public void setName(@Nonnull String name) {
        this.name = name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Nonnull
    public Equipment getEquipment() {
        return equipment;
    }

    @Nonnull
    public Heroes getHero() {
        return hero;
    }

    public void equip(Player player) {
        getEquipment().equip(player);
    }

}
