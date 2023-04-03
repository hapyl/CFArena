package me.hapyl.fight.game.effect.storage;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class BleedEffect extends GameEffect {

    private final double damage = 2.0d;

    public BleedEffect() {
        super("Bleed");
        setPositive(false);
    }

    @Override
    public void onStart(Player player) {
        Chat.sendMessage(player, "&c&l∲ &7You are bleeding!");
    }

    @Override
    public void onStop(Player player) {
        Chat.sendMessage(player, "&c&l∲ &aThe bleeding has stopped!");
    }

    @Override
    public void onTick(Player player, int tick) {
        if (tick == 0) {
            GamePlayer.getPlayer(player).damage(damage, EnumDamageCause.BLEED);

            player.getWorld()
                    .spawnParticle(
                            Particle.BLOCK_CRACK,
                            player.getLocation(),
                            10,
                            0.5d,
                            0.5d,
                            0.5d,
                            0.0d,
                            Material.REDSTONE_BLOCK.createBlockData()
                    );
        }
    }
}
