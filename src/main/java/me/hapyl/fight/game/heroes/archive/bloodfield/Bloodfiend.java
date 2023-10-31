package me.hapyl.fight.game.heroes.archive.bloodfield;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.ComplexHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.UltimateCallback;
import me.hapyl.fight.game.heroes.archive.bloodfield.impel.Impel;
import me.hapyl.fight.game.heroes.archive.bloodfield.impel.ImpelInstance;
import me.hapyl.fight.game.heroes.archive.bloodfield.impel.Type;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.playerskin.PlayerSkin;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.bloodfiend.BloodCup;
import me.hapyl.fight.game.talents.archive.bloodfiend.TwinClaws;
import me.hapyl.fight.game.talents.archive.bloodfiend.candlebane.Candlebane;
import me.hapyl.fight.game.talents.archive.bloodfiend.candlebane.CandlebaneTalent;
import me.hapyl.fight.game.talents.archive.bloodfiend.chalice.BloodChalice;
import me.hapyl.fight.game.talents.archive.bloodfiend.chalice.BloodChaliceTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.ui.UIComplexComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.entity.EntityUtils;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Bat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class Bloodfiend extends Hero implements ComplexHero, Listener, UIComplexComponent {

    public static final int BLOOD_SLOT = 4;

    @DisplayField public final short impelTimes = 5;
    @DisplayField public final int impelDuration = 40;
    @DisplayField public final int impelCd = 10;
    @DisplayField public final double impelDamage = 30.0d;

    private final Map<GamePlayer, BloodfiendData> playerData = Maps.newConcurrentMap();

    public Bloodfiend() {
        super("Bloodfiend");

        setDescription("""
                A vampire prince with a sunscreen.
                """);
        setArchetype(Archetype.DAMAGE);
        setItem("5aa29ea961757dc3c90bfabf302c5abe9d308fb4a7d3864e5788ad2cc9160aa2");
        setSkin(new PlayerSkin(
                "ewogICJ0aW1lc3RhbXAiIDogMTY2MTQwMDI0MTc2NiwKICAicHJvZmlsZUlkIiA6ICI4YTg3NGJhNmFiZDM0ZTc5OTljOWM1ODMwYWYyY2NmNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJSZXphMTExIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzVhYTI5ZWE5NjE3NTdkYzNjOTBiZmFiZjMwMmM1YWJlOWQzMDhmYjRhN2QzODY0ZTU3ODhhZDJjYzkxNjBhYTIiCiAgICB9CiAgfQp9",
                "u7oD4NYj9J7UzMV/GZ3oScp3E6ci7+YI3DsDlTzVfsHKB5yWNEyZPttL09dMDWyJY1kdC8hsK8i5xhF5BaC/2pj/f3SNndzkrflEYrwUr8/1GVXpejIEVpb+SNqImpjsxWY3bLVQHaQ47WjMzvfrQ/gaEMKp3vDmjqST4gWKPxyk6hEHAudA1evE95QSjKX+ayMc822WQPOlPsqcFIZ/f/HYivYl9FQ4HbSyRfK2iI3Ibb0Mwg7BDcJuvkxdnIpkwBz1Hu3SH77dcpXZtLvIBc7dy41zJOMhUzyqkFFVrid5GvgTgb2o+iJ9mSNfVxN9khpG2q15lofdfIseijpq3QP2rAhdl3uX7DqT/CzOzfXP/9FGQaGuYySkNRlbt1WLfWJN9sHWK/jyz1nhV+JwJvwg/uV4Cor9q1jr01cv/FsWIUwSHLnXndIOEileCKnqlo6G/FtTU4Rgd1C5CryBUhY1WMc+HPk38wmWo6HzNOlhT1HltiPjb4kpSUP+vz5LTtplOqwomw/XBp/wuXuS2ijzCVo6lovtUzra5lsGa9EijHPreXt2dEHy68bTZBt2Os4BeWCMTz58d4wvSvC/hHNXdd/asx1CcW288HFxWRxoNLLawanDILCZLdRln4MwlGP1IruOuK0wJOkP3kxqHJdCL51psBPWDpPTzW0VC9c="
        ));

        final HeroAttributes attributes = getAttributes();
        attributes.setHealth(80.0d);

        final Equipment equipment = getEquipment();

        equipment.setChestPlate(99, 8, 16, TrimPattern.SILENCE, TrimMaterial.NETHERITE);
        equipment.setLeggings(28, 3, 7);
        equipment.setBoots(5, 3, 23, TrimPattern.HOST, TrimMaterial.NETHERITE);

        setWeapon(new Weapon(Material.GHAST_TEAR).setName("Vampire's Fang").setDamage(6.0d).setAttackSpeed(0.5d));

        final UltimateTalent ultimate = new UltimateTalent("Impel", 50)
                .setItem(Material.MOOSHROOM_SPAWN_EGG)
                .setDuration(impelDuration * impelTimes)
                .setCastDuration(30);

        ultimate.setDescription("""
                After a short casting time, impel all &cbitten &cenemies&7 for {duration}.
                &8&o;;While casting, transform into a bat and fly freely.
                                
                While impelled, enemies must obey &b%s &7of your commands.
                                
                If failed to obey a command, they will suffer &c%s&7 damage.
                """, impelTimes, impelDamage);

        setUltimate(ultimate);
    }

    @Override
    public void onStop() {
        playerData.values().forEach(BloodfiendData::reset);
        playerData.clear();
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                playerData.forEach((player, data) -> {
                    data.tick();
                });
            }
        }.runTaskTimer(1, 1);
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        getData(player).cooldownFlight(true);
    }

    @Override
    public void onRespawn(@Nonnull GamePlayer player) {
        getData(player).cooldownFlight(true);
    }

    @Nullable
    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        final GamePlayer player = input.getDamagerAsPlayer();
        final LivingGameEntity entity = input.getEntity();
        final EnumDamageCause cause = input.getDamageCause();

        if (player == null || cause != EnumDamageCause.ENTITY_ATTACK) {
            return DamageOutput.OK;
        }

        entity.addEffect(GameEffectType.IMMOVABLE, 1);

        final BloodfiendData data = getData(player);
        data.addBlood();

        if (!(entity instanceof GamePlayer victim)) {
            return DamageOutput.OK;
        }

        data.addSucculence(victim);

        // Blood Chalice
        final BloodChaliceTalent bloodChalice = getThirdTalent();
        final BloodChalice taunt = bloodChalice.getTaunt(player);

        if (taunt != null && taunt.target.equals(victim)) {
            final double damage = input.getDamage();
            final double healing = damage * bloodChalice.healingPercent / 100;

            player.heal(healing);
        }

        return DamageOutput.OK;
    }

    @Nonnull
    public BloodfiendData getData(GamePlayer player) {
        return playerData.computeIfAbsent(player, BloodfiendData::new);
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        final BloodfiendData data = playerData.remove(player);

        if (data == null) {
            return;
        }

        data.reset();
    }

    @Override
    public UltimateCallback castUltimate(@Nonnull GamePlayer player) {
        final BloodfiendData data = getData(player);
        final Set<GamePlayer> succulencePlayers = data.getSucculencePlayers();
        final Location location = player.getLocation().add(0.0d, 0.5d, 0.0d);

        final HumanNPC npc = new HumanNPC(player.getLocation(), "", player.getName());

        npc.showAll();
        npc.setSitting(true);

        // Draw particles
        succulencePlayers.forEach(target -> {
            drawTentacleParticles(location, target.getLocation());
        });

        // Spawn bats
        final Set<Bat> fxBats = Sets.newHashSet();
        final Location eyeLocation = player.getEyeLocation();

        for (int i = 0; i < 10; i++) {
            fxBats.add(Entities.BAT.spawn(eyeLocation, self -> {
                self.setAwake(true);
                self.setInvulnerable(true);
            }));
        }

        player.addEffect(GameEffectType.INVISIBILITY, 10000, true);

        final Bat playerBat = Entities.BAT.spawn(eyeLocation, self -> {
            self.setAwake(true);
            self.setInvulnerable(true);

            EntityUtils.setCollision(self, EntityUtils.Collision.DENY, player.getPlayer());
        });

        final float flySpeed = player.getFlySpeed();

        player.teleport(eyeLocation);

        player.setFlySpeed(0.05f);
        player.setAllowFlight(true);
        player.setFlying(true);

        // Fx
        final GameTask batTask = new GameTask() {
            @Override
            public void run() {
                playerBat.teleport(player.getLocation());

                // Fx
                PlayerLib.spawnParticle(player.getLocation(), Particle.SMOKE_NORMAL, 2, 0.15d, 0.15d, 0.15d, 0.0f);
            }
        }.runTaskTimer(0, 1);

        PlayerLib.playSound(location, Sound.ENTITY_BAT_TAKEOFF, 0.25f);

        playSoundAtTick(
                location,
                Sound.ENTITY_ELDER_GUARDIAN_CURSE,
                0.75f,
                0.1f,
                0, 2, 3, 5, 6, 9, 10, 12
        );

        return new UltimateCallback() {
            @Override
            public void callback(@Nonnull GamePlayer player) {
                fxBats.forEach(Bat::remove);
                fxBats.clear();

                player.setFlySpeed(flySpeed);
                player.setAllowFlight(false);
                player.setFlying(false);

                data.cooldownFlight(true);

                player.removeEffect(GameEffectType.INVISIBILITY);

                npc.remove();
                playerBat.remove();
                batTask.cancelIfActive();
            }
        };
    }

    @Override
    public void useUltimate(@Nonnull GamePlayer player) {
        getData(player).newImpelInstance(this, player).nextImpel(0);
    }

    @Override
    public TwinClaws getFirstTalent() {
        return (TwinClaws) Talents.TWIN_CLAWS.getTalent();
    }

    @Override
    public CandlebaneTalent getSecondTalent() {
        return (CandlebaneTalent) Talents.CANDLEBANE.getTalent();
    }

    @Override
    public BloodChaliceTalent getThirdTalent() {
        return (BloodChaliceTalent) Talents.BLOOD_CHALICE.getTalent();
    }

    @Override
    public BloodCup getFourthTalent() {
        return (BloodCup) Talents.BLOOD_CUP.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.SUCCULENCE.getTalent();
    }

    // impel handles
    @EventHandler()
    public void handleImpelClick(PlayerInteractEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());

        if (player == null) {
            return;
        }

        final float pitch = player.getLocation().getPitch();

        workImpel(player, (impel, gamePlayer) -> {
            if (pitch <= -50) {
                impel.complete(gamePlayer, Type.CLICK_UP);
            }
            else if (pitch >= 40) {
                impel.complete(gamePlayer, Type.CLICK_DOWN);
            }
        });

    }

    @EventHandler()
    public void handleImpelSneak(PlayerToggleSneakEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());

        if (player == null) {
            return;
        }

        workImpel(player, (impel, gamePlayer) -> {
            impel.complete(gamePlayer, Type.SNEAK);
        });
    }

    @EventHandler()
    public void handleImpelJump(PlayerMoveEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());
        final Location to = ev.getTo();
        final Location from = ev.getFrom();

        if (player == null || to == null || (to.getY() <= from.getY()) || player.isOnGround()) {
            return;
        }

        workImpel(player, (impel, gamePlayer) -> {
            impel.complete(gamePlayer, Type.JUMP);
        });
    }

    @Nullable
    public ImpelInstance getPlayerImpel(GamePlayer player) {
        for (BloodfiendData data : playerData.values()) {
            final ImpelInstance impel = data.getImpelInstance();
            if (impel == null || !impel.isPlayer(player)) {
                continue;
            }

            return impel;
        }

        return null;
    }

    public void workImpel(GamePlayer player, BiConsumer<Impel, GamePlayer> consumer) {
        if (player == null) {
            return;
        }

        final ImpelInstance impelInstance = getPlayerImpel(player);

        if (impelInstance == null) {
            return;
        }

        final Impel impel = impelInstance.getImpel();

        if (impel == null) {
            return;
        }

        consumer.accept(impel, player);
    }

    @EventHandler()
    public void handleFlight(PlayerToggleFlightEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());

        if (player == null) {
            return;
        }

        final BloodfiendData data = getData(player);

        if (!validatePlayer(player)) {
            return;
        }

        // Ultimate bat
        if (isUsingUltimate(player)) {
            return;
        }

        if (data.isFlying()) {
            data.stopFlying();
            return;
        }

        if (data.hasFlightCooldown()) {
            ev.setCancelled(true);
            return;
        }

        data.startFlying();
    }

    @Nullable
    @Override
    public List<String> getStrings(@Nonnull GamePlayer player) {
        final BloodfiendData data = getData(player);
        final int succulencePlayers = data.getSucculencePlayersCount();
        final int flightCooldown = data.getFlightCooldown();

        final CandlebaneTalent twinClaws = getSecondTalent();
        final BloodChaliceTalent bloodChalice = getThirdTalent();

        final Candlebane pillar = twinClaws.getTaunt(player);
        final BloodChalice chalice = bloodChalice.getTaunt(player);

        return List.of(
                succulencePlayers > 0 ? "&c&l🦇 &f" + succulencePlayers : "",
                flightCooldown > 0 ? "&2&l\uD83D\uDD4A &f" + CFUtils.decimalFormatTick(flightCooldown) : "",
                pillar != null ? "&6&lⅡ &f" + CFUtils.decimalFormatTick(pillar.getTimeLeft()) : "",
                chalice != null ? "&4&l🍷 &f" + CFUtils.decimalFormatTick(chalice.getTimeLeft()) : ""
        );
    }

    public void drawTentacleParticles(Location start, Location end) {
        final double distance = start.distance(end);
        final double step = distance / 30 * 2;

        final Vector vector = end.clone().subtract(start).toVector().normalize().multiply(step);
        final Location location = start.clone();

        new TickingGameTask() {
            private double d;

            @Override
            public void run(int tick) {
                if (d >= distance) {
                    cancel();
                    return;
                }

                final double y = Math.sin(d / distance * Math.PI * 3) * 0.75d;

                location.add(vector);
                location.add(0, y, 0);

                PlayerLib.spawnParticle(location, Particle.LAVA, 1, 0.1d, 0.1d, 0.1d, 0.0f);
                PlayerLib.spawnParticle(location, Particle.FLAME, 1);
                PlayerLib.spawnParticle(location, Particle.SMALL_FLAME, 3, 0.1d, 0.1d, 0.1d, 0.05f);

                location.subtract(0, y, 0);
                d += step;
            }
        }.runTaskTimer(0, 1);
    }

    private void playSoundAtTick(Location location, Sound sound, final float pitch, float pitchIncrease, int... ticks) {
        final int lastTick = ticks[ticks.length - 1];

        new TickingGameTask() {
            private float currentPitch = pitch;

            @Override
            public void run(int tick) {
                if (tick >= lastTick) {
                    cancel();
                    return;
                }

                if (isTick()) {
                    PlayerLib.playSound(location, sound, currentPitch);
                    currentPitch += pitchIncrease;
                }
            }

            private boolean isTick() {
                for (int tick : ticks) {
                    if (getTick() == tick) {
                        return true;
                    }
                }

                return false;
            }
        }.runTaskTimer(0, 1);
    }

}
