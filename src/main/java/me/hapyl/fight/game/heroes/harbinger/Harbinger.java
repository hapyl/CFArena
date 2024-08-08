package me.hapyl.fight.game.heroes.harbinger;

import com.google.common.collect.Sets;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.event.custom.GameDeathEvent;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.EquipmentSlots;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.heroes.UltimateResponse;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.harbinger.MeleeStance;
import me.hapyl.fight.game.talents.harbinger.StanceData;
import me.hapyl.fight.game.talents.harbinger.TidalWaveTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.BowWeapon;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Set;

public class Harbinger extends Hero implements Listener, UIComponent, PlayerDataHandler<RiptideStatus>, DisplayFieldProvider {

    @DisplayField private final long meleeRiptideAmount = 100;
    @DisplayField private final long rangeRiptideAmount = 150;

    @DisplayField private final double ultimateMeleeDamage = 40;
    @DisplayField private final double ultimateMeleeRadius = 4.0d;
    @DisplayField private final double ultimateMeleeRiptideDamageMultiplier = 1.5d;

    @DisplayField private final double ultimateRangeDamage = 25;
    @DisplayField private final int ultimateRangeRiptide = Tick.fromSecond(20);
    @DisplayField private final double ultimateRangeRadius = 4.0d;

    private final PlayerDataMap<RiptideStatus> riptideStatus = PlayerMap.newDataMap(RiptideStatus::new);
    private final Set<Arrow> ultimateArrows = Sets.newHashSet();

    public Harbinger(@Nonnull Heroes handle) {
        super(handle, "Harbinger");

        setDescription("""
                She is a harbinger of unknown organization. Nothing else is known.
                """);

        setArchetypes(Archetype.STRATEGY);
        setAffiliation(Affiliation.UNKNOWN);
        setGender(Gender.FEMALE);

        setMinimumLevel(5);
        setItem("22a1ac2a8dd48c371482806b3963571952997a5712806e2c8060b8e7777754");

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(82, 82, 76);
        equipment.setLeggings(54, 48, 48);
        equipment.setBoots(183, 183, 180);

        setWeapon(new BowWeapon().setDamage(2.0d).setName("Bow").setDescription("Just a normal bow."));
        setUltimate(new HarbingerUltimate());
    }

    @EventHandler()
    public void handleProjectileHitEvent(ProjectileHitEvent ev) {
        final Projectile entity = ev.getEntity();

        if (!(entity instanceof Arrow arrow)) {
            return;
        }

        final ProjectileSource shooter = arrow.getShooter();

        if (!(shooter instanceof Player player)) {
            return;
        }

        final GamePlayer gamePlayer = CF.getPlayer(player);

        if (gamePlayer == null) {
            return;
        }

        if (!ultimateArrows.contains(arrow)) {
            return;
        }

        ultimateArrows.remove(arrow);

        final Location location = arrow.getLocation();
        executeUltimateArrow(gamePlayer, location);
    }

    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getDamagerAsPlayer();
        final LivingGameEntity entity = instance.getEntity();

        if (player == null || !instance.isEntityAttack()) {
            return;
        }

        final MeleeStance stance = getFirstTalent();
        final boolean isStanceActive = stance.isActive(player);

        // If in melee stance only execute on CRIT
        if (isStanceActive && instance.isCrit()) {
            addRiptide(player, instance.getEntity(), meleeRiptideAmount, false);
            executeRiptideSlashIfPossible(player, entity);
        }
    }

    @Override
    public void processDamageAsDamagerProjectile(@Nonnull DamageInstance instance, @Nonnull Projectile projectile) {
        if (!(projectile instanceof Arrow arrow) || !arrow.isCritical()) {
            return;
        }

        final GamePlayer player = instance.getDamagerAsPlayer();
        final LivingGameEntity entity = instance.getEntity();

        if (player == null) {
            return;
        }

        executeRiptideSlashIfPossible(player, entity);
        addRiptide(player, entity, rangeRiptideAmount, false);
    }

    public void executeRiptideSlashIfPossible(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity) {
        getPlayerData(player).executeRiptideSlash(entity);
    }

    public void addRiptide(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, long amount, boolean force) {
        getPlayerData(player).setRiptide(entity, amount, force);
    }

    @EventHandler()
    public void handleGameDeathEvent(GameDeathEvent ev) {
        final LivingGameEntity entity = ev.getEntity();

        for (RiptideStatus status : riptideStatus.values()) {
            if (status.isAffected(entity)) {
                status.stop(entity);
            }
        }
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        player.setItem(EquipmentSlots.ARROW, new ItemStack(Material.ARROW));
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                riptideStatus.values().forEach(RiptideStatus::tick);
            }
        }.runTaskTimer(0, 1);
    }

    @Override
    public MeleeStance getFirstTalent() {
        return (MeleeStance) Talents.STANCE.getTalent();
    }

    @Override
    public TidalWaveTalent getSecondTalent() {
        return (TidalWaveTalent) Talents.TIDAL_WAVE.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.RIPTIDE.getTalent();
    }

    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final StanceData data = getFirstTalent().getData(player);

        if (data == null) {
            return "";
        }

        return "&f⚔: &l%ss&f/&l%ss".formatted(
                BukkitUtils.roundTick(data.getDurationTick()),
                BukkitUtils.roundTick(getFirstTalent().getMaxDuration())
        );
    }

    @Nonnull
    @Override
    public PlayerDataMap<RiptideStatus> getDataMap() {
        return riptideStatus;
    }

    private void executeUltimateArrow(GamePlayer player, Location location) {
        // Affect
        Collect.nearbyEntities(location, ultimateRangeRadius).forEach(entity -> {
            if (player.isSelfOrTeammate(entity)) {
                return;
            }

            entity.setLastDamager(player);
            entity.damage(ultimateRangeDamage, EnumDamageCause.RIPTIDE);

            addRiptide(player, entity, ultimateRangeRiptide, false);
        });

        // Fx
        player.playWorldSound(location, Sound.ENTITY_DROWNED_HURT, 0.0f);

        new TimedGameTask(10) {
            private double d;

            @Override
            public void run(int tick) {
                final double spread = Math.PI * 2 / 8;
                final float speed = 0.0f + (0.5f / maxTick * tick);

                for (int index = 1; index <= 4; index++) {
                    final double x = Math.sin(d + spread * index) * ultimateRangeRadius;
                    final double y = Math.sin(Math.PI / 8 * tick) * 0.2d;
                    final double z = Math.cos(d + spread * index) * ultimateRangeRadius;

                    location.add(x, y, z);

                    player.spawnWorldParticle(location, Particle.GLOW, 5, 0.01f, 0.01f, 0.01f, speed);
                    //player.spawnWorldParticle(location, Particle.CRIT, 1);

                    location.subtract(x, y, z);
                }

                d += Math.PI * 2 / maxTick;
            }
        }.runTaskTimer(0, 1);
    }

    private class HarbingerUltimate extends UltimateTalent {
        public HarbingerUltimate() {
            super("Crowned Mastery", 70);

            setDescription("""
                    Gather the surrounding energy to execute a &cfatal strike&7 based on your &ncurrent&7 &nstance&7.
                                    
                    &6In Range Stance
                    Shoots a magic arrow in front of you that explodes upon impact, dealing moderate &cAoE damage&7 and applying %1$s.
                                    
                    &6In Melee Stance
                    Performs a slash around you that deals significant &cAoE damage&7.
                                    
                    If an enemy is affected by %1$s, the damage is &cincreased&7 and the %1$s is removed.
                    """.formatted(Named.RIPTIDE));

            setItem(Material.DIAMOND);
            setDuration(40);

            copyDisplayFieldsFrom(Harbinger.this);
        }

        @Nonnull
        @Override
        public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
            final Location location = player.getEyeLocation();
            final RiptideStatus riptide = getPlayerData(player);

            // Stance Check
            final boolean isMeleeStance = getFirstTalent().isActive(player);

            // Melee Stance
            if (isMeleeStance) {
                new TickingGameTask() {
                    private final double mathPi2 = Math.PI * 2;

                    private double d = 0;
                    private double radius = 1.0d;

                    @Override
                    public void run(int tick) {
                        if (d > mathPi2) {
                            cancel();
                            return;
                        }

                        final double x = Math.sin(d) * radius;
                        final double y = Math.sin(Math.toRadians(tick)) * 0.25d;
                        final double z = Math.cos(d) * radius;

                        location.add(x, y, z);

                        // Affect
                        Collect.nearbyEntities(location, 2.0d).forEach(entity -> {
                            if (player.isSelfOrTeammate(entity)) {
                                return;
                            }

                            final boolean affected = riptide.isAffected(entity);
                            riptide.stop(entity);

                            entity.setLastDamager(player);
                            entity.damage(
                                    affected ? ultimateMeleeDamage * ultimateMeleeRiptideDamageMultiplier : ultimateMeleeDamage,
                                    EnumDamageCause.RIPTIDE
                            );
                            entity.setNoDamageTicks(30); // Prevent multiple damage
                        });

                        // Fx
                        player.spawnWorldParticle(location, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0.1f);
                        player.spawnWorldParticle(location, Particle.FALLING_WATER, 3, 0.5, 0, 0.5, 0.01f);

                        player.playWorldSound(location, Sound.ENTITY_DROWNED_HURT, (float) (0.5f + (1.0f / Math.PI * 2 * d)));

                        location.subtract(x, y, z);

                        d += Math.PI / 5;
                        radius += (ultimateMeleeRadius - 1) / mathPi2 / d;
                    }
                }.runTaskTimer(15, 1);
            }
            // Ranged Stance
            else {
                final Vector direction = location.getDirection();

                final Arrow arrow = player.getWorld()
                        .spawnArrow(
                                location.add(direction.setY(0.0d).multiply(2)).add(0.0d, 3.0d, 0.0d),
                                direction.normalize().multiply(0.75d).setY(0.25),
                                0.15f,
                                0
                        );

                arrow.setShooter(player.getPlayer());
                arrow.setCritical(false);
                arrow.setColor(Color.AQUA);

                ultimateArrows.add(arrow);

                // Fx
                player.playWorldSound(Sound.ITEM_CROSSBOW_SHOOT, 0.75f);
            }

            // Fx
            player.addEffect(Effects.SLOW, 2, 20);
            player.playWorldSound(Sound.BLOCK_CONDUIT_AMBIENT, 2.0f);

            return UltimateResponse.OK;
        }
    }
}
