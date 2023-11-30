package me.hapyl.fight.game.entity;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public enum Outline {
    CLEAR {
        @Override
        public void set(@Nonnull Player player) {
            final World world = player.getWorld();

            player.setWorldBorder(world.getWorldBorder());
        }
    },
    RED {
        @Override
        public void set(@Nonnull Player player) {
            final WorldBorder worldBorder = Bukkit.createWorldBorder();

            worldBorder.setCenter(player.getLocation());
            worldBorder.setSize(1000);
            worldBorder.setWarningDistance(2000);
            worldBorder.setDamageAmount(0.0d);

            player.setWorldBorder(worldBorder);
        }
    };

    public void set(@Nonnull Player player) {
    }
}