package me.hapyl.fight.game.cosmetic.skin;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface EffectHandler {

    void onTick(Player player, int tick);

    void onKill(Player player, LivingEntity victim);

    void onDeath(Player player, LivingEntity killer);

    void onMove(Player player, Location to);

    void onStandingStill(Player player);

}
