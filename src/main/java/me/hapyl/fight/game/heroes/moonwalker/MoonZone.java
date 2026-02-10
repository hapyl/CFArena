package me.hapyl.fight.game.heroes.moonwalker;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.hologram.Hologram;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.fx.EntityFollowingParticle;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.talents.moonwalker.MoonPassive;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;

public class MoonZone implements Ticking, Removable {

    private static final ItemStack[] MOON_ENERGY_ITEMS = {
            ItemStack.of(Material.END_STONE),
            ItemStack.of(Material.END_STONE_BRICKS),
            ItemStack.of(Material.PURPLE_STAINED_GLASS),
            ItemStack.of(Material.PURPUR_BLOCK),
            ItemStack.of(Material.PURPUR_PILLAR),
    };

    protected final GamePlayer player;
    protected final Location centre;
    protected final double size;

    private final Hologram hologram;
    private final MoonPassive passive;
    private final int duration;

    protected double energy;

    protected int tick;
    protected double theta;

    public MoonZone(@Nonnull GamePlayer player, @Nonnull Location centre, int duration, double size, int energy) {
        this.player = player;
        this.centre = centre;
        this.duration = duration;
        this.size = size;
        this.energy = energy;
        this.passive = HeroRegistry.MOONWALKER.getPassiveTalent();

        this.hologram = LocationHelper.offset(centre, 0, hologramOffset(), 0, Hologram::ofArmorStand);

        // Show to self (Maybe show to teammates as well?)
        this.hologram.show(player.getEntity());
    }

    @Override
    public boolean shouldRemove() {
        return energy <= 0 || tick >= duration;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void tick() {
        tick++;

        if (energy <= 0) {
            return;
        }

        // Update hologram
        // hologram.setLines(
        //         "&6&l%.0f %s &8| &b&l%.1fs".formatted(energy, Named.MOONLIT_ENERGY.getPrefix(), (duration - tick) / 20d)
        // );

        // Rush energy towards the owner if within the range
        if (tick % passive.energyConversionRate == 0 && player.isSneaking()) {
            final double distance = player.getLocation().distance(centre);

            if (distance <= size) {
                final Entity entity = createEnergyEntity();
                final double energyToTransfer = Math.min(energy, passive.energyConversion);

                energy -= energyToTransfer;

                new EntityFollowingParticle(3, centre, player) {
                    @Override
                    public double distanceSquared() {
                        return 0.15d;
                    }

                    @Override
                    public double mlFactor() {
                        return 0.15707963267948966d;
                    }

                    @Override
                    public void draw(int tick, @Nonnull Location location) {
                        location.setYaw(location.getYaw() + 5f);
                        entity.teleport(location);

                        // Fx
                        final int aliveTicks = player.aliveTicks();

                        if (tick % 2 == 0) {
                            player.playWorldSound(location, Sound.ENTITY_SHULKER_BULLET_HIT, 0.8f + (0.05f * (aliveTicks % 6)));
                            player.playWorldSound(location, Sound.ENTITY_CHICKEN_EGG, 0.5f + (0.05f * (aliveTicks % 6)));
                        }
                    }

                    @Override
                    public void onTaskStop() {
                        // Delay removal to allow interpolation to reach the player
                        GameTask.runLater(
                                () -> {
                                    entity.remove();
                                }, 2
                        );
                    }

                    @Override
                    public void onHit(@Nonnull Location location) {
                        // Transfer energy
                        player.getPlayerData(HeroRegistry.MOONWALKER).incrementMoonEnergy(energyToTransfer);
                    }
                }.runTaskTimer(0, 1);
            }
        }

        // Fx
        for (int i = 0; i < 5; i++) {
            playFx();
        }

        // Sfx
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void remove() {
        hologram.destroy();
    }

    protected double hologramOffset() {
        return 3;
    }

    private void playFx() {
        final double increment = Math.PI / 48;
        final double distance = size * (0.1d * Math.sin(Math.toRadians(tick)) + 0.9d);

        final double x = Math.sin(theta) * distance;
        final double y = Math.sin(Math.toRadians(tick * 5)) * 0.3d + 0.5d;
        final double z = Math.cos(theta) * distance;

        LocationHelper.offset(
                centre, x, y, z, () -> player.spawnWorldParticle(centre, Particle.CRIT, 1)
        );

        theta += increment;
    }

    private ArmorStand createEnergyEntity() {
        return Entities.ARMOR_STAND_MARKER.spawn(
                centre, self -> {
                    self.setInvisible(true);
                    self.getEquipment().setHelmet(CollectionUtils.randomElementOrFirst(MOON_ENERGY_ITEMS));

                    CFUtils.setAttributeValue(self, Attribute.SCALE, player.random.nextDouble(0.25d, 0.75d));
                }
        );
    }

}
