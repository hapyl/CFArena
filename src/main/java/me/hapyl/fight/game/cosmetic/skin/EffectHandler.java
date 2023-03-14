package me.hapyl.fight.game.cosmetic.skin;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface EffectHandler {

    EffectHandler NONE = new EffectHandler() {
        @Override
        public void onTick(Player player, int tick) {

        }

        @Override
        public void onKill(Player player, LivingEntity victim) {

        }

        @Override
        public void onDeath(Player player, LivingEntity killer) {

        }

        @Override
        public void onMove(Player player, Location to) {

        }

        @Override
        public void onStandingStill(Player player) {

        }
    };

    void onTick(Player player, int tick);

    void onKill(Player player, LivingEntity victim);

    void onDeath(Player player, LivingEntity killer);

    void onMove(Player player, Location to);

    void onStandingStill(Player player);

}
