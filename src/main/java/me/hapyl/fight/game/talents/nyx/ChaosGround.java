package me.hapyl.fight.game.talents.nyx;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.math.Geometry;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Removable;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.ShutdownAction;
import me.hapyl.fight.game.task.TickingStepGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.EntityList;
import me.hapyl.fight.util.ItemStackRandomizedData;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Set;

public class ChaosGround extends Talent implements Listener {

    @DisplayField private final short orbCount = 6;

    @DisplayField private final double baseDistance = 3.0d;
    @DisplayField private final double maxDistance = 10.0d;
    @DisplayField private final double attackThreshold = 0.33d;

    @DisplayField private final short dropletCount = 3;

    private final double durationRaw = 6.5f * 20;
    private final int speed = 3;

    @DisplayField private final double duration = durationRaw / speed;

    private final Set<ChaosDroplet> chaosDroplets = Sets.newHashSet();

    public ChaosGround() {
        super("Chaos Expansion");

        setDescription("""
                Start channeling a chaos spell.
                
                After a moderate casting time, creates an explosion in large AoE, dealing damage and impairing enemies within.
                
                Also spawn %s chaos droplets, that heal teammates and deals damage to enemies.
                """.formatted(dropletCount));

        setItem(Material.CHORUS_FRUIT);

        setCooldownSec(7.5f);
    }

    @Override
    public void onStop(@Nonnull GamePlayer player) {
        chaosDroplets.clear();
    }

    @EventHandler
    public void handleItemPickup(EntityPickupItemEvent ev) {
        final LivingEntity entity = ev.getEntity();
        final Item item = ev.getItem();

        final ChaosDroplet droplet = chaosDroplets.stream()
                .filter(d -> d.item == item)
                .findFirst()
                .orElse(null);

        if (droplet == null) {
            return;
        }

        if (!(entity instanceof Player player)) {
            ev.setCancelled(true);
            return;
        }
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        final EntityList<ArmorStand> orbs = new EntityList<>(orbCount);

        new TickingStepGameTask(3) {
            private double d = 0.0d;

            @Override
            public boolean tick(int tick) {
                if (tick >= duration || player.isDeadOrRespawning()) {
                    orbs.clear();
                    return true;
                }

                final double offset = Math.PI / orbCount;
                final double dc = baseDistance * tick * 2 / duration;

                double distance = baseDistance - dc;

                // Attack
                if (distance <= attackThreshold) {
                    distance = maxDistance;
                }

                for (int i = 0; i < orbs.size(); i++) {
                    final double x = Math.sin(d * offset + i) * distance;
                    final double y = (Math.atan(Math.PI / 2 * d) * 0.75d) - 1.5d;
                    final double z = Math.cos(d * offset + i) * distance;

                    location.add(x, y, z);

                    orbs.getOrSet(i, () -> Entities.ARMOR_STAND.spawn(location, self -> {
                        self.setMarker(true);
                        self.setSmall(true);
                        self.getEquipment().setHelmet(new ItemStack(Material.CRYING_OBSIDIAN));
                    })).teleport(location);

                    location.subtract(x, y, z);
                }

                d += Math.PI / 16;

                // Attack
                if (distance >= maxDistance) {
                    runLater(() -> {
                        // Affect
                        Collect.nearbyEntities(location, maxDistance, player::isNotSelfOrTeammate)
                                .forEach(entity -> {

                                });

                        // Spawn droplets
                        for (int i = 0; i < dropletCount; ++i) {
                            // Pick random location
                        }

                        // Fx
                        Geometry.drawSphere(location, maxDistance * 2, maxDistance,
                                loc -> player.spawnWorldParticle(loc, Particle.CRIT, 1)
                        );

                        orbs.clear();
                    }, 5).setShutdownAction(ShutdownAction.IGNORE);
                    return true;
                }

                return false;
            }
        }.runTaskTimer(0, 1);
        return Response.OK;
    }

    private static class ChaosDroplet implements Removable {

        private final GamePlayer player;
        private final Item item;

        private ChaosDroplet(GamePlayer player, Location location) {
            this.player = player;
            this.item = location.getWorld().dropItem(
                    location,
                    new ItemStackRandomizedData(Material.STONE_SWORD),
                    self -> {
                        self.setCanMobPickup(false);
                        self.setCanPlayerPickup(true);
                    }
            );
        }

        @Override
        public void remove() {
            this.item.remove();
        }
    }
}
