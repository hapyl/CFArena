package me.hapyl.fight.game.cosmetic.death;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.Type;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class ScaryDookieCosmetic extends Cosmetic {
    public ScaryDookieCosmetic(@Nonnull Key key) {
        super(key, "Scary Dookie", Type.DEATH);

        setDescription("""
                The ultimate scare!
                """
        );

        setRarity(Rarity.RARE);
        setIcon(Material.COCOA_BEANS);
    }

    @Override
    public void onDisplay(@Nonnull Display display) {
        final Item item = display.dropItem(Material.COCOA_BEANS, 6000);
        final Player player = display.getPlayer();
        final String name = player == null ? "Someones" : player.getName();

        item.setCustomName(Chat.format("&c&l%s's Dookie".formatted(name)));
        item.setCustomNameVisible(true);
        item.setVelocity(new Vector(0.0d, 0.2d, 0.0d));

        display.sound(Sound.ENTITY_PLAYER_BURP, 1.25f);
        display.sound(Sound.ENTITY_HORSE_SADDLE, 1.75f);
    }
}
