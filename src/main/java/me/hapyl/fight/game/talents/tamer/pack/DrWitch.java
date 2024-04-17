package me.hapyl.fight.game.talents.tamer.pack;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTargetWitch;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestHealableRaider;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Witch;

import javax.annotation.Nonnull;

public class DrWitch extends TamerPack {

    @DisplayField private final int witchHealingPeriod = 100;
    @DisplayField private final double witchHealingThreshold = 3.0d;
    @DisplayField private final double witchHealingAmount = 5.0d;

    public DrWitch() {
        super("Dr. Witch", """
                Periodically splashes a &ahealing&7 potion on a &eteammate&7 with the lowest health.
                """, TalentType.SUPPORT);

        attributes.setHealth(30);

        setDurationSec(30);
    }

    @Override
    public void onSpawn(@Nonnull ActiveTamerPack pack, @Nonnull Location location) {
        pack.createEntity(location, Entities.WITCH, entity -> new DrWitchEntity(pack, entity));
    }

    @Nonnull
    @Override
    public String toString(ActiveTamerPack pack) {
        final DrWitchEntity entity = pack.getFirstEntityOfType(DrWitchEntity.class);

        if (entity == null) {
            return "";
        }

        final int nextPotion = entity.nextPotion;
        return " &a⚗ " + (nextPotion == 0 ? "&lREADY!" : CFUtils.decimalFormatTick(nextPotion));
    }

    public record WitchData(GameTeam team, GamePlayer target, double healing) {
    }

    private class DrWitchEntity extends TamerEntity<Witch> {

        private int nextPotion;

        public DrWitchEntity(@Nonnull ActiveTamerPack pack, @Nonnull Witch entity) {
            super(pack, entity);

            this.targetClosestEntities = false;
            this.ai.removeAllGoals(goal -> {
                return goal instanceof PathfinderGoalNearestAttackableTargetWitch || goal instanceof PathfinderGoalNearestHealableRaider;
            });

            this.nextPotion = witchHealingPeriod;
        }

        @Override
        public void tick(int index) {
            super.tick(index);

            if (nextPotion > 0) {
                nextPotion--;
            }

            final GamePlayer target = getHealingTarget();

            if (nextPotion == 0 && target != null) {
                throwHealingPotion(target);
                nextPotion = witchHealingPeriod;
            }
        }

        private void throwHealingPotion(GamePlayer target) {
            if (target == null) {
                return;
            }

            lookAt(target.getLocation());

            final ThrownPotion potion = entity.launchProjectile(ThrownPotion.class);
            potion.setItem(new ItemBuilder(Material.SPLASH_POTION).setPotionColor(Color.RED).asIcon());

            getHero().potionMap.put(potion, new WitchData(team, player, scaleUltimateEffectiveness(player, witchHealingAmount)));
        }

        private GamePlayer getHealingTarget() {
            GamePlayer target = player;
            double minHealth = player.getHealth();

            for (GamePlayer player : team.getPlayers()) {
                final double playerHealth = player.getHealth();

                if (playerHealth < minHealth) {
                    target = player;
                    minHealth = playerHealth;
                }
            }

            if (target.getLocation().distance(getLocation()) >= witchHealingThreshold) {
                return null;
            }

            return target;
        }

    }
}
