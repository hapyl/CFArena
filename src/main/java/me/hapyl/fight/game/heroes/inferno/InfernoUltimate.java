package me.hapyl.fight.game.heroes.inferno;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.terminology.EnumTerm;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public class InfernoUltimate extends UltimateTalent implements Listener {
    private static final ItemStack MAGMA_TEXTURE = ItemBuilder.playerHeadUrl("721d0930bd61fea4cb9027b00e94e13d62029c524ea0b3260c747457ba1bcfa1").asIcon();
    private static final int FX_STANDS = 5;

    @DisplayField protected final short pillarHealth = 8;
    @DisplayField protected final short pillarHeight = 4;
    protected final int pillarHealthHeightDifference = pillarHealth / pillarHeight;

    @DisplayField protected final int explosionDelay = Tick.fromSeconds(12);
    @DisplayField(suffix = " blocks") protected final double explosionRadius = 50;
    @DisplayField(percentage = true, suffix = " of Max Health") protected final double damage = 20.0;

    private final Set<FirePillar> firePillars;

    public InfernoUltimate(Inferno inferno) {
        super(inferno, "Fire Pillar", 70);

        this.firePillars = Sets.newHashSet();

        setDescription("""
                Start channeling a &6Fire Pillar&7 that lands nearby after {cast}.
                
                Upon landing, it must be destroyed within &b{explosionDelay}&7 or enemies within &a{explosionRadius}&7 takes damage equal to &4%.0f%%&7 of their %s as %s.
                """.formatted(damage * 100, AttributeType.MAX_HEALTH, EnumTerm.TRUE_DAMAGE));

        setType(TalentType.DAMAGE);
        setMaterial(Material.MAGMA_BLOCK);

        setCastDurationSec(5.0f);
        setCooldownSec(60);
    }

    @EventHandler
    public void handlePlayerInteractEvent(PlayerInteractEvent ev) {
        final GamePlayer player = CF.getPlayer(ev);
        final Block clickedBlock = ev.getClickedBlock();

        if (player == null || clickedBlock == null || ev.getHand() == EquipmentSlot.OFF_HAND || ev.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        final FirePillar pillar = pillarByBlock(clickedBlock);

        if (pillar == null) {
            return;
        }

        pillar.hitPillar(player);
        ev.setCancelled(true);
    }

    @Nonnull
    @Override
    public UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged) {
        final Location landLocation = getLocationLocation(player.getLocation());

        if (landLocation == null) {
            return error("Could not find where to put the pillar!");
        }

        return new Instance(player, landLocation);
    }

    protected FirePillar pillarByBlock(Block block) {
        for (FirePillar pillar : firePillars) {
            if (pillar.hasBlock(block)) {
                return pillar;
            }
        }

        return null;
    }

    protected void createPillar(FirePillar pillar) {
        firePillars.add(pillar);
        pillar.runTaskTimer(0, 1);
    }

    protected void removePillar(FirePillar pillar) {
        firePillars.remove(pillar);
        pillar.cancel();
    }

    private Location getLocationLocation(Location location) {
        int tries = 10;

        while (tries-- > 0) {
            final Location landLocation = LocationHelper.randomAround(location, 3d);

            if (canLand(landLocation)) {
                return BukkitUtils.centerLocation(landLocation);
            }
        }

        return null;
    }

    private boolean canLand(Location location) {
        final Block block = location.getBlock();

        for (int i = 0; i < pillarHeight; i++) {
            final Block relative = block.getRelative(BlockFace.UP, i);

            if (!relative.getType().isAir()) {
                return false;
            }
        }

        return true;
    }

    private class Instance extends UltimateInstance {
        private final GamePlayer player;
        private final List<ArmorStand> fxStands;
        private final Location landLocation;

        private Location startLocation;

        private Instance(GamePlayer player, Location landLocation) {
            this.player = player;
            this.fxStands = Lists.newArrayList();
            this.landLocation = landLocation;
            this.landLocation.setYaw(0.0f);
            this.landLocation.setPitch(0.0f);

            final Location location = fxLocation();

            for (int i = 0; i < FX_STANDS; i++) {
                this.fxStands.add(spawnArmorStand(location));
            }
        }

        @Override
        public void onExecute() {
            onPlayerDied(player);

            // Spawn the pillar
            createPillar(new FirePillar(InfernoUltimate.this, player, landLocation));
        }

        @Override
        public void onPlayerDied(@Nonnull GamePlayer player) {
            CFUtils.clearCollectionAnd(fxStands, ArmorStand::remove);
        }

        @Override
        public void onCastTick(int tick) {
            final Location location = fxLocation();

            // Offset the location
            final int castDuration = getCastDuration();
            final double flyAnimationStart = castDuration * 0.6d;
            final double progress = (double) tick / castDuration;

            final double spread = Math.PI * 2 / fxStands.size();
            final double r = Math.toRadians(tick) * 18;

            double yawRad = Math.toRadians(location.getYaw()) + Math.PI / 2;

            if (tick >= flyAnimationStart) {
                // Init "start" location
                if (startLocation == null) {
                    startLocation = location;
                }

                final double t = (tick - flyAnimationStart) / (castDuration - flyAnimationStart);
                final double ease = t < 0.5d ? 2 * t * t : -1 + (4 - 2 * t) * t;
                final double sinY = Math.sin(Math.PI * t) * 2.5d;

                final double x = startLocation.getX() + (landLocation.getX() - startLocation.getX()) * ease;
                final double y = startLocation.getY() + (landLocation.getY() - startLocation.getY()) * ease + sinY;
                final double z = startLocation.getZ() + (landLocation.getZ() - startLocation.getZ()) * ease;

                yawRad = Math.toRadians(tick) * 15;
                location.set(x, y, z);
            }

            int i = 0;
            for (ArmorStand stand : fxStands) {
                final double groundX = Math.cos(r + i * spread) * 0.4d;
                final double groundZ = Math.sin(r + i * spread) * 0.4d;

                final double x = groundX * -Math.sin(yawRad);
                final double z = groundX * Math.cos(yawRad);

                LocationHelper.offset(
                        location,
                        x, groundZ, z, () -> {
                            stand.teleport(location);

                            // Fx
                            LocationHelper.offset(
                                    location, 0, 1, 0, () -> player.spawnWorldParticle(location, Particle.SMOKE, 1, 0.1d, 0.1d, 0.1d, 0.05f)
                            );
                        }
                );
                ++i;
            }

            // Fx
            if (tick % 2 == 0) {
                player.playWorldSound(location, Sound.BLOCK_LAVA_POP, (float) (1.0f + 1.0f * progress));
                player.playWorldSound(location, Sound.ITEM_FIRECHARGE_USE, (float) (0.75f + 1.25f * progress));
            }

            // Spawn lava particles on land location
            player.spawnWorldParticle(landLocation, Particle.LAVA, 1, 0.4d, 0.1d, 0.4d, 0.01f);
        }

        private Location fxLocation() {
            return player.getEyeLocation().add(0d, 0.25d, 0d);
        }

        private ArmorStand spawnArmorStand(Location location) {
            return Entities.ARMOR_STAND_MARKER.spawn(
                    location, self -> {
                        self.getEquipment().setHelmet(MAGMA_TEXTURE);
                        self.setSmall(true);
                        self.setInvisible(true);
                        self.setHeadPose(new EulerAngle(Math.toRadians(45d), 0d, 0d));
                    }
            );
        }
    }
}
