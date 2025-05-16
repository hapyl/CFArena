package me.hapyl.fight.game.talents.knight;


import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.MaterialData;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SlownessPotion extends Talent {

    private final ItemStack potionItem = new ItemBuilder(Material.SPLASH_POTION).setPotionColor(Color.BLACK)
            .setPotionMeta(PotionEffectType.SLOWNESS, 5, 80, Color.GRAY)
            .build();

    public SlownessPotion(@Nonnull Key key) {
        super(key, "Slowness Potion");

        setDescription("""
                A little bottle that can cause a lot of troubles.
                
                Throw a slowing potion if front of that slows enemies in small AoE.
                """
        );

        setMaterial(MaterialData.of(Material.SPLASH_POTION, builder -> builder.setPotionColor(Color.GRAY)));
        
        setCooldownSec(12);
    }

    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        final ThrownPotion potion = player.launchProjectile(ThrownPotion.class);

        potion.setItem(potionItem);
        potion.setShooter(player.getEntity());

        return Response.OK;
    }
}
