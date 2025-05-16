package me.hapyl.fight.game.heroes.mage;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.talents.Timed;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Formatted;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public abstract class MageSpell implements Formatted, Timed {
    protected static final ModifierSource modifierSource = new ModifierSource(Key.ofString("mage_spell"), true);
    
    protected final String name;
    protected final String description;
    
    private final ItemStack item;
    
    private int duration;
    
    public MageSpell(@Nonnull Key key, @Nonnull String name, @Nonnull String description, @Nonnull Material material) {
        this.name = name;
        this.description = description;
        
        this.item = new ItemBuilder(material, key)
                .addClickEvent(player -> CF.getPlayerOptional(player).ifPresent(this::execute))
                .setName(name + CFUtils.RIGHT_CLICK)
                .addLore("&8Ancient Spell")
                .addLore()
                .addTextBlockLore(description)
                .build();
    }
    
    @Override
    public int getDuration() {
        return duration;
    }
    
    @Override
    public Timed setDuration(int duration) {
        this.duration = duration;
        return null;
    }
    
    @Nonnull
    public ItemStack getSpellItem() {
        return item;
    }
    
    public final Response execute(@Nonnull GamePlayer player) {
        final Mage mage = HeroRegistry.MAGE;
        
        player.setUsingUltimate(getDuration());
        useSpell(player);
        
        player.snapToWeapon();
        
        // Remove items
        mage.spellWyvernHeart.removeItem(player);
        mage.spellDragonSkin.removeItem(player);
        
        // Fx
        player.sendMessage("&aYou have used &l%s&a!".formatted(name));
        player.playWorldSound(Sound.ITEM_FLINTANDSTEEL_USE, 0.0f);
        
        return Response.OK;
    }
    
    @Nonnull
    @Override
    public String getFormatted() {
        return """
               &a&l%s
               %s
               """.formatted(name, description);
    }
    
    protected abstract void useSpell(@Nonnull GamePlayer player);
    
    private void removeItem(GamePlayer player) {
        if (this.item == null) {
            return;
        }
        
        player.getInventory().remove(this.item.getType());
    }
}
