package me.hapyl.fight.game.talents.archive.knight;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class SlownessPotion extends Talent {

    private final ItemStack potionItem = new ItemBuilder(Material.SPLASH_POTION).setPotionColor(Color.BLACK)
            .setPotionMeta(PotionEffectType.SLOW, 5, 80, Color.GRAY)
            .build();

    public SlownessPotion() {
        super("Slowness Potion", """
                A little bottle that can cause a lot of troubles.
                                              
                Throw a slowing potion if front of that slows enemies in small AoE.
                """);

        setItem(Material.SPLASH_POTION, builder -> builder.setPotionColor(Color.GRAY));
        setCooldownSec(12);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final ThrownPotion potion = player.launchProjectile(ThrownPotion.class);

        potion.setItem(potionItem);
        potion.setShooter(player.getPlayer());

        return Response.OK;
    }
}
