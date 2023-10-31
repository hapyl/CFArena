package me.hapyl.fight.item;

import me.hapyl.fight.registry.EnumRegistry;
import me.hapyl.fight.registry.Registry;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

// this is very temporary
public enum Items implements EnumRegistry<Item> {

    RED_DRAGON_PET(
            ItemBuilder
                    .playerHeadUrl("2232383d46a55b3fa38a3214bea9e96b6377f39ef06613b0d81ab3a86c50c869")
                    .setName("&7[Lvl 100] &6Sandworm")
                    .addTextBlockLore("""
                            &8Combat Pet
                                                        
                            &7Ferocity: &a+70
                            &7Strength: &c+30
                                                        
                            &6Legendary Find
                            &7When finding a scarab, there is &a10%
                            &7chance to find a higher tier one.
                                                        
                            &6Sand Dune
                            &7Increases your &eⓈ Swing Range&7 by &63&7.
                                                        
                            &6Perk 2
                            &7Blah blah blah

                            &b&lMAX LEVEL
                                                        
                            &eRight-click to add this pet to
                            &eyour pet menu!
                                                        
                            &6&lLEGENDARY
                            """)
                    .asIcon()
    ),

    ;

    private final ItemStack item;

    Items(ItemStack stack) {
        this.item = stack;
    }

    @Nonnull
    public Item getItem() {
        return null;
    }

    @Nonnull
    @Override
    public Registry<Item> getRegistry() {
        return Registry.ITEM_REGISTRY;
    }
}
