package me.hapyl.fight.game.heroes.aurora;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.event.custom.GameDeathEvent;
import me.hapyl.fight.event.custom.ProjectilePostLaunchEvent;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.aurora.AuroraArrowTalent;
import me.hapyl.fight.game.talents.aurora.GuardianAngel;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.task.player.PlayerTickingGameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.BowWeapon;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;
import java.util.Set;

public class Aurora extends Hero implements PlayerDataHandler<AuroraData>, Listener, UIComponent {

    private final PlayerDataMap<AuroraData> auroraDataMap = PlayerMap.newDataMap(AuroraData::new);
    private final Set<AuroraArrowData> arrowDataSet = Sets.newHashSet();
    private final Particle.DustTransition dustTransition = new Particle.DustTransition(
            Color.fromRGB(1, 126, 213), Color.fromRGB(141, 0, 196), 1
    );

    public Aurora(@Nonnull Key key) {
        super(key, "Aurora");

        setDescription("""
                An angel-like creature from above the skies.
                """);

        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.SUPPORT, Archetype.HEXBANE);
        profile.setGender(Gender.FEMALE);
        profile.setRace(Race.UNKNOWN);

        setItem("9babb9fbe50a84b31f68e749b438d4c8f7f58618aec3e769243aa660ce4440fb");

        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(78, 252, 235, TrimPattern.RAISER, TrimMaterial.AMETHYST);
        equipment.setLeggings(73, 161, 171, TrimPattern.SILENCE, TrimMaterial.AMETHYST);
        equipment.setBoots(204, 110, 235, TrimPattern.SILENCE, TrimMaterial.AMETHYST);

        final HeroAttributes attributes = getAttributes();
        attributes.setDefense(80);
        attributes.setSpeed(110);
        attributes.setCritChance(-100);

        setWeapon(
                BowWeapon.of(Key.ofString("celestial"), "Celestial", """
                        A unique bow of celestial origins.
                        """, 2)
        );

        setUltimate(new AuroraUltimate());
    }

    @EventHandler
    public void handleEntityDeath(GameDeathEvent ev) {
        final LivingGameEntity entity = ev.getEntity();

        for (AuroraData data : getDataMap().values()) {
            data.remove(entity);
        }
    }

    @EventHandler
    public void handleEntityDamage(GameDamageEvent ev) {
        final LivingGameEntity entity = ev.getEntity();

        if (!(entity instanceof GamePlayer player)) {
            return;
        }

        if (!validatePlayer(player)) {
            return;
        }

        final AuroraData data = getPlayerData(player);

        if (data.bond == null) {
            return;
        }

        data.breakBond("You took damage!");
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        player.setArrowItem();
    }

    @Override
    public void onStart(@Nonnull GameInstance instance) {
        new TickingGameTask() {
            @Override
            public void run(int tick) {
                // Tick arrows
                arrowDataSet.removeIf(data -> data.arrow().isDead() || data.player().isDeadOrRespawning());
                arrowDataSet.forEach(data -> data.type().onTick(data.player(), data.arrow(), tick));

                // Tick data
                auroraDataMap.values().forEach(data -> {
                    // Tick arrow type
                    if (data.arrow != null) {
                        final String string = data.arrow.getString(data.arrowCount);
                        data.player.sendSubtitle(string, 0, 5, 0);
                    }

                    // Tick buff
                    data.buffMap.values().forEach(EtherealSpirit::tick);
                });

                // Tick teleport
                // Doing sqrt every tick is wack
                if (modulo(5)) {
                    getAlivePlayers().forEach(player -> {
                        final AuroraData data = getPlayerData(player);
                        final GuardianAngel passiveTalent = getPassiveTalent();

                        data.target = Collect.targetEntityRayCastIgnoreLoS(
                                player,
                                passiveTalent.maxDistance,
                                passiveTalent.lookupRadius,
                                player::isTeammate
                        );
                    });
                }
            }
        }.runTaskTimer(0, 1);
    }

    @Override
    public boolean processTeammateDamage(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, DamageInstance instance) {
        instance.setDamage(0.0);
        instance.setCancelled(true);

        return false; // Don't cancel damage to not spam the "Cannot damage teammates."
    }

    @EventHandler()
    public void handleSneak(PlayerToggleSneakEvent ev) {
        final GamePlayer player = CF.getPlayer(ev);

        if (!validatePlayer(player)) {
            return;
        }

        final AuroraData data = getPlayerData(player);
        final LivingGameEntity target = data.target;

        if (target == null || data.hasBond()) {
            return;
        }

        final GuardianAngel talent = getPassiveTalent();

        if (talent.hasCd(player)) {
            return;
        }

        talent.startCdIndefinitely(player);

        player.addEffect(EffectType.SLOW, 10, talent.teleportDelay);

        new PlayerTickingGameTask(player) {

            @Override
            public void run(int tick) {
                final LivingGameEntity newTarget = data.target;

                if (newTarget == null) {
                    cancelTeleport("Lost focus!");
                    return;
                }

                if (target != newTarget) {
                    cancelTeleport("The target has changed!");
                    return;
                }

                if (target.isDeadOrRespawning()) {
                    cancelTeleport("The target has died!");
                    cancel();
                    return;
                }

                // Teleport
                if (tick > talent.teleportDelay) {
                    final Location teleportLocation = getTeleportLocation(target.getLocation());
                    final Location playerLocation = player.getLocation();

                    teleportLocation.setYaw(playerLocation.getYaw());
                    teleportLocation.setPitch(playerLocation.getPitch());

                    player.teleport(teleportLocation);
                    player.playWorldSound(teleportLocation, Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1.0f);

                    // Heal
                    Collect.nearbyEntities(teleportLocation, talent.healingRadius, player::isTeammate) // don't head Aurora
                            .forEach(entity -> {
                                entity.heal(talent.healing, player);
                            });

                    // Fx
                    spawnParticles(teleportLocation, 5, 0.3f, 0.3f, 0.3f);
                    spawnParticles(playerLocation, 5, 0.3f, 0.3f, 0.3f);

                    new TickingGameTask() {

                        private double distance;

                        @Override
                        public void run(int tick) {
                            if (distance >= talent.healingRadius) {
                                cancel();
                                return;
                            }

                            for (double d = 0; d < Math.PI * 2; d += Math.PI / (8 * (1 + distance / talent.healingRadius))) {
                                final double x = Math.sin(d) * distance;
                                final double y = Math.atan(Math.toRadians(tick) * 10) * 0.5d;
                                final double z = Math.cos(d) * distance;

                                LocationHelper.offset(teleportLocation, x, y, z, () -> {
                                    spawnParticles(teleportLocation, 2, 0.1f, 0.1f, 0.1f);
                                });
                            }

                            distance += Math.PI / 16;
                        }
                    }.runTaskTimer(0, 1);

                    talent.startCd(player);

                    cancel();
                    return;
                }

                final float progress = (float) tick / talent.teleportDelay;
                final int maxCrystals = 5;
                final int crystalCount = (int) (maxCrystals * progress);

                player.sendTitle("&6\uD83D\uDC7C",
                        "&5❖".repeat(crystalCount) + "&b❖".repeat(maxCrystals - crystalCount),
                        0, 5, 0
                );

                // Fx
                if (modulo(2)) {
                    final float pitch = 0.75f + progress;

                    // Play the sound to both the Aurora and the target
                    player.playSound(Sound.BLOCK_AMETHYST_BLOCK_BREAK, pitch);
                    target.playSound(Sound.BLOCK_AMETHYST_BLOCK_BREAK, pitch);
                }
            }

            private void cancelTeleport(String reason) {
                player.sendTitle("&6\uD83D\uDC7C", "&cCancelled! &4" + reason, 5, 10, 5);
                player.playSound(Sound.ENTITY_ALLAY_HURT, 0.75f);

                talent.startCd(player, talent.getCooldown() / 2);
                cancel();
            }
        }.runTaskTimer(0, 1);
    }

    public void spawnParticles(@Nonnull Location location, int count, double x, double y, double z) {
        location.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, location, count, x, y, z, 0, dustTransition);
    }

    @EventHandler()
    public void handleShot(ProjectilePostLaunchEvent ev) {
        final GamePlayer player = ev.getShooter();

        if (!validatePlayer(player)) {
            return;
        }

        final Projectile projectile = ev.getProjectile();

        if (!(projectile instanceof Arrow arrow)) {
            return;
        }

        final AuroraData data = getPlayerData(player);
        final AuroraArrowTalent arrowType = data.arrow;

        if (arrowType == null) {
            return;
        }

        arrowDataSet.add(new AuroraArrowData(player, arrowType, arrow));
        arrowType.onShoot(player, arrow);

        data.arrowCount--;

        if (data.arrowCount <= 0) {
            data.setArrow(null);
        }
    }

    @Override
    public void processDamageAsDamagerProjectile(@Nonnull DamageInstance instance, @Nonnull Projectile projectile) {
        final GamePlayer player = instance.getDamagerAsPlayer();

        if (player == null || !(projectile instanceof Arrow arrow)) {
            return;
        }

        final AuroraArrowData data = CollectionUtils.findAndRemove(arrowDataSet, d -> d.arrow() == arrow);

        if (data == null) {
            return;
        }

        final LivingGameEntity entity = instance.getEntity();

        data.type().onHit(player, entity, instance);
    }

    @Override
    public Talent getFirstTalent() {
        return TalentRegistry.CELESTE_ARROW;
    }

    @Override
    public Talent getSecondTalent() {
        return TalentRegistry.ETHEREAL_ARROW;
    }

    @Override
    public GuardianAngel getPassiveTalent() {
        return TalentRegistry.GUARDIAN_ANGEL;
    }

    @Nonnull
    @Override
    public PlayerDataMap<AuroraData> getDataMap() {
        return auroraDataMap;
    }

    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final AuroraData data = getPlayerData(player);
        final GuardianAngel passiveTalent = getPassiveTalent();
        final LivingGameEntity target = data.target;

        if (passiveTalent.hasCd(player)) {
            return "&6\uD83D\uDC7C &f" + CFUtils.formatTick(passiveTalent.getCdTimeLeft(player));
        }

        return target != null ? "&6\uD83D\uDC7C &e%s".formatted(target.getName()) : "";
    }

    @Nonnull
    @Override
    public AuroraUltimate getUltimate() {
        return (AuroraUltimate) super.getUltimate();
    }

    private Location getTeleportLocation(Location initialLocation) {
        final Location location = BukkitUtils.newLocation(initialLocation);
        location.add(1, 0, 1);

        for (int x = 0; x < 2; x++) {
            for (int z = 0; z < 2; z++) {
                location.subtract(x, 0, z);

                if (checkLocation(location)) {
                    return location;
                }

                location.add(x, 0, z);
            }
        }

        return initialLocation;
    }

    private boolean checkLocation(Location location) {
        final Block block = location.getBlock();

        if (!block.getRelative(BlockFace.DOWN).getType().isSolid()) {
            return false;
        }

        if (!block.isPassable()) {
            return false;
        }

        // Check for air block at the head instead of passable
        return block.getRelative(BlockFace.UP).isEmpty();
    }

    public class AuroraUltimate extends UltimateTalent {

        @DisplayField public final double healing = 0.5d;
        @DisplayField public final int cooldown = Tick.fromSecond(25);
        @DisplayField public final double maxStrayDistance = 250;

        @DisplayField private final String duration = "Infinite";

        public AuroraUltimate() {
            super(Aurora.this, "Divine Intervention", 70);

            setDescription("""
                    Creates a &bCelestial Bond&7 with the &etarget&a teammate&7.
                    
                    &6Celestial Bond
                    Rapidly &a&nheals&7 the bonded &ateammate&7 and constantly applies the &nmaximum&7 level %s.
                    
                    While the bond is &nactive&7, Aurora gains the ability to &ffloat&7, but &closes&7 the ability to use &btalents&7 or &4weapons&7.
                    
                    The bond lasts &nindefinitely&7, unless Aurora takes a &nsingle&7 &ninstance&7 of &cdamage&7 or the &fline of sight&7 breaks.
                    &8&o;;Aurora is immune to fall and suffocation damage while the bond is active.
                    """.formatted(Named.ETHEREAL_SPIRIT)
            );

            setType(TalentType.SUPPORT);
            setItem(Material.PRISMARINE_CRYSTALS);

            setSound(Sound.BLOCK_AMETHYST_BLOCK_STEP, 0.0f);
        }


        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player) {
            final AuroraData data = getPlayerData(player);
            final LivingGameEntity target = data.target;

            if (target == null) {
                return error("Not targeting a teammate!");
            }

            if (!player.hasLineOfSight(target)) {
                return error("Not in light of sight!");
            }

            // Make sure the target isn't already bonded
            for (AuroraData otherData : auroraDataMap.values()) {
                final CelestialBond otherBond = otherData.bond;

                if (otherBond != null && otherBond.getEntity().equals(target)) {
                    return error("This teammate is already bonded with %s!".formatted(otherBond.getPlayer().getName()));
                }
            }

            return execute(() -> {
                // Reset arrow
                data.setArrow(null);

                // Reset all buffs
                data.buffMap.clear(EtherealSpirit::remove);

                // Create bond
                data.bond = new CelestialBond(this, data, player, target);

                player.setUsingUltimate(true);
                startCdIndefinitely(player);
            });
        }
    }

}
