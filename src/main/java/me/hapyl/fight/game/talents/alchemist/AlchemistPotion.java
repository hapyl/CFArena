package me.hapyl.fight.game.talents.alchemist;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Described;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.alchemist.ActivePotion;
import me.hapyl.fight.game.heroes.alchemist.AlchemistData;
import me.hapyl.fight.game.talents.Timed;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public abstract class AlchemistPotion implements Timed, Described {

    private final String name;
    private final int intoxication;
    private final Color potionColor;

    private final int duration = Tick.fromSeconds(10);

    private String description = "set description";
    private ItemStack potionItem;

    public AlchemistPotion(String name, int intoxication, Color potionColor) {
        this.name = name;
        this.intoxication = intoxication;
        this.potionColor = potionColor;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    @Deprecated
    public Timed setDuration(int duration) {
        return this;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    public Key getNameAsKey() {
        return Key.ofString(name.replace(" ", "_").toLowerCase());
    }
    
    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nonnull String description) {
        this.description = description;
    }

    @Nonnull
    public ItemStack getPotionItem() {
        if (potionItem == null) {
            potionItem = new ItemBuilder(Material.POTION)
                    .setName(name)
                    .addLore("&8Abyssal Potion")
                    .addLore()
                    .addTextBlockLore(description)
                    .addLore()
                    .addLore("%s: &c+%s".formatted(Named.ABYSS_CORROSION, intoxication))
                    .setPotionColor(potionColor)
                    .asIcon();
        }

        return potionItem;
    }

    public int intoxication() {
        return intoxication;
    }

    @Nonnull
    public abstract ActivePotion use(@Nonnull AlchemistData data, @Nonnull GamePlayer player);

}
