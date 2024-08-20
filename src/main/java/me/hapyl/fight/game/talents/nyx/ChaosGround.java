package me.hapyl.fight.game.talents.nyx;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.math.Geometry;
import me.hapyl.eterna.module.math.geometry.Drawable;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.nyx.ChaosDroplet;
import me.hapyl.fight.game.heroes.nyx.NyxData;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.task.ShutdownAction;
import me.hapyl.fight.game.task.TickingStepGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.EntityList;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import javax.annotation.Nonnull;

public class ChaosGround extends Talent implements Listener {

    @DisplayField private final short orbCount = 6;

    @DisplayField private final double baseDistance = 3.0d;
    @DisplayField private final double maxDistance = 10.0d;
    @DisplayField private final double attackThreshold = 0.33d;

    @DisplayField private final short dropletCount = 3;

    private final double durationRaw = 6.5f * 20;
    private final int speed = 3;

    @DisplayField private final double duration = durationRaw / speed;

    // Storing in three different variables for display fields
    @DisplayField private final double healingFirst = 7.0d;
    @DisplayField private final double healingSecond = 5.0d;
    @DisplayField private final double healingThird = 3.0d;

    @DisplayField private final double damage = 15.0d;

    public ChaosGround(@Nonnull DatabaseKey key) {
        super(key, "Chaos Expansion");

        setDescription("""
                Start channeling a chaos spell.
                
                After a short casting time, creates an &4explosion&7 in &clarge AoE&7, dealing &cdamage&7 and &eimpairing&7 enemies within.
                
                Also spawn &b%s &4chaos droplets&7, that &a&nheal&7 &ateammates&7 and &cdeals damage&7 to &cenemies&7.
                &8&o;;The healing decreases with each droplet.
                """.formatted(dropletCount)
        );

        setItem(Material.CHORUS_FRUIT);

        setCooldownSec(15.0f);
    }

    @EventHandler
    public void handleItemPickup(EntityPickupItemEvent ev) {
        final LivingEntity entity = ev.getEntity();
        final Item item = ev.getItem();

        final NyxData dataWithDroplet = HeroRegistry.NYX.getDataMap()
                .values()
                .stream()
                .filter(data -> data.getDroplet(item) != null)
                .findFirst()
                .orElse(null);

        if (dataWithDroplet == null) {
            return;
        }

        final ChaosDroplet droplet = dataWithDroplet.getDroplet(item);

        if (droplet == null) {
            return;
        }

        if (!(entity instanceof Player player)) {
            ev.setCancelled(true); // Don't allow non-player entities picking up droplets
            return;
        }

        final GamePlayer gamePlayer = CF.getPlayer(player);

        if (gamePlayer == null) {
            return;
        }

        // Don't allow picking up if player is full health
        if (gamePlayer.isFullHealth()) {
            ev.setCancelled(true);
            return;
        }

        // Store the current droplet count before removing the droplet
        final int dropletCount = dataWithDroplet.dropletCount();

        //chaosDroplets.remove(droplet);
        dataWithDroplet.removeDroplet(droplet);
        ev.setCancelled(true);

        // Affecting here because yes
        final GamePlayer nyx = dataWithDroplet.player;
        final Location location = droplet.getLocation();

        if (nyx.isSelfOrTeammate(gamePlayer)) {
            final double healing = getHealing(dropletCount);

            gamePlayer.heal(healing, nyx);

            // Fx
            gamePlayer.playWorldSound(location, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1.75f);
            gamePlayer.playWorldSound(location, Sound.ITEM_FIRECHARGE_USE, 2.0f);
        }
        else {
            gamePlayer.damage(damage, nyx, EnumDamageCause.CHAOS);

            gamePlayer.spawnWorldParticle(location, Particle.RAID_OMEN, 10, 0.5, 0.25, 0.5, 0.025f);
            gamePlayer.playWorldSound(Sound.ENTITY_EVOKER_FANGS_ATTACK, 0.75f);
        }
    }

    private double getHealing(int dropletCount) {
        return switch (dropletCount) {
            case 3 -> healingFirst;
            case 2 -> healingSecond;
            case 1 -> healingThird;
            default -> 0;
        };
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        final NyxData data = player.getPlayerData(HeroRegistry.NYX);
        final EntityList<ArmorStand> orbs = new EntityList<>(orbCount);

        // Remove previous droplets
        data.remove();

        new TickingStepGameTask(3) {
            private double d = 0.0d;
            private int lastSfxTick = 0;

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

                    // Fx
                    player.playWorldSound(location, Sound.ENTITY_WITHER_SHOOT, 0.75f);
                    player.playWorldSound(location, Sound.ENTITY_WARDEN_HURT, 0.75f);
                }

                for (int i = 0; i < orbs.size(); i++) {
                    final double x = Math.sin(d * offset + i) * distance;
                    final double y = (Math.atan(Math.PI / 2 * d) * 0.75d) - 1.5d;
                    final double z = Math.cos(d * offset + i) * distance;

                    location.add(x, y, z);

                    orbs.getOrSet(i, () -> Entities.ARMOR_STAND.spawn(location, self -> {
                        self.setMarker(true);
                        self.setSmall(true);
                        self.setInvisible(true);
                        self.getEquipment().setHelmet(ChaosDroplet.item());
                    })).teleport(location);

                    location.subtract(x, y, z);
                }

                // Fx
                if (lastSfxTick != tick) {
                    lastSfxTick = tick;
                    player.playWorldSound(location, Sound.ENTITY_ENDERMAN_HURT, (float) (0.5f + (1.0f * distance / maxDistance)));
                }

                d += Math.PI / 16;

                // Attack
                if (distance >= maxDistance) {
                    runLater(() -> {
                        // Affect
                        Collect.nearbyEntities(location, maxDistance, player::isNotSelfOrTeammate)
                                .forEach(entity -> {
                                    entity.damageNoKnockback(damage, player, EnumDamageCause.CHAOS);

                                    if (entity instanceof GamePlayer playerEntity) {
                                        TalentRegistry.BLINDING_CURSE.scaryWither(playerEntity);
                                    }
                                });

                        // Spawn droplets
                        for (int i = 0; i < dropletCount; ++i) {
                            // Pick random location
                            final double x = player.random.nextDoubleBool(maxDistance - 0.5d);
                            final double z = player.random.nextDoubleBool(maxDistance - 0.5d);

                            location.add(x, 0, z);
                            data.createDroplet(location);
                            location.subtract(x, 0, z);
                        }

                        // Fx
                        for (int i = 0; i < orbs.size(); i++) {
                            final ArmorStand current = orbs.get(i);
                            final ArmorStand next = orbs.get(i + 1 >= orbs.size() ? 0 : i + 1);

                            // This will never happen, just to shut up the compiler
                            if (current == null || next == null) {
                                continue;
                            }

                            Geometry.drawLine(
                                    current.getLocation().add(0, 1, 0),
                                    next.getLocation().add(0, 1, 0),
                                    0.5d, new Drawable() {
                                        @Override
                                        public void draw(@Nonnull Location location) {
                                            HeroRegistry.NYX.drawParticle(location);
                                        }
                                    }
                            );
                        }

                        // Fx
                        player.playWorldSound(location, Sound.ENTITY_WARDEN_DEATH, 0.75f);

                        orbs.clear();
                    }, 5).setShutdownAction(ShutdownAction.IGNORE);
                    return true;
                }

                return false;
            }
        }.runTaskTimer(0, 1);
        return Response.OK;
    }

}
