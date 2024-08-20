package me.hapyl.fight.game.heroes.bloodfield;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.entity.EntityUtils;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.player.PlayerSkin;
import me.hapyl.eterna.module.reflect.npc.HumanNPC;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.event.custom.TalentUseEvent;
import me.hapyl.fight.fx.EntityFollowingParticle;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.bloodfield.impel.Impel;
import me.hapyl.fight.game.heroes.bloodfield.impel.ImpelInstance;
import me.hapyl.fight.game.heroes.bloodfield.impel.Type;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.bloodfiend.TwinClaws;
import me.hapyl.fight.game.talents.bloodfiend.candlebane.CandlebaneTalent;
import me.hapyl.fight.game.talents.bloodfiend.chalice.BloodChaliceTalent;
import me.hapyl.fight.game.talents.bloodfiend.taunt.Taunt;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.ui.UIComplexComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.displayfield.DisplayField;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * todo:
 *  Candlebane might need a range increase
 *  Didn't really notify Blood Chalice effect
 */
public class Bloodfiend extends Hero implements ComplexHero, Listener, UIComplexComponent {

    @DisplayField public final short impelTimes = 3;
    @DisplayField public final double impelNonPlayerDamage = 50;
    @DisplayField public final int impelDuration = 30;
    @DisplayField public final int impelCd = 10;
    @DisplayField public final double impelDamage = 25d;

    private final Map<GamePlayer, BloodfiendData> playerData = Maps.newConcurrentMap();

    public Bloodfiend(@Nonnull DatabaseKey key) {
        super(key, "Bloodfiend");

        setArchetypes(Archetype.DAMAGE);
        setAffiliation(Affiliation.CHATEAU);
        setGender(Gender.MALE);
        setRace(Race.VAMPIRE);

        setDescription("""
                A vampire prince with a sunscreen.
                """);

        setItem("5aa29ea961757dc3c90bfabf302c5abe9d308fb4a7d3864e5788ad2cc9160aa2");
        setSkin(new PlayerSkin(
                "ewogICJ0aW1lc3RhbXAiIDogMTY2MTQwMDI0MTc2NiwKICAicHJvZmlsZUlkIiA6ICI4YTg3NGJhNmFiZDM0ZTc5OTljOWM1ODMwYWYyY2NmNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJSZXphMTExIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzVhYTI5ZWE5NjE3NTdkYzNjOTBiZmFiZjMwMmM1YWJlOWQzMDhmYjRhN2QzODY0ZTU3ODhhZDJjYzkxNjBhYTIiCiAgICB9CiAgfQp9",
                "u7oD4NYj9J7UzMV/GZ3oScp3E6ci7+YI3DsDlTzVfsHKB5yWNEyZPttL09dMDWyJY1kdC8hsK8i5xhF5BaC/2pj/f3SNndzkrflEYrwUr8/1GVXpejIEVpb+SNqImpjsxWY3bLVQHaQ47WjMzvfrQ/gaEMKp3vDmjqST4gWKPxyk6hEHAudA1evE95QSjKX+ayMc822WQPOlPsqcFIZ/f/HYivYl9FQ4HbSyRfK2iI3Ibb0Mwg7BDcJuvkxdnIpkwBz1Hu3SH77dcpXZtLvIBc7dy41zJOMhUzyqkFFVrid5GvgTgb2o+iJ9mSNfVxN9khpG2q15lofdfIseijpq3QP2rAhdl3uX7DqT/CzOzfXP/9FGQaGuYySkNRlbt1WLfWJN9sHWK/jyz1nhV+JwJvwg/uV4Cor9q1jr01cv/FsWIUwSHLnXndIOEileCKnqlo6G/FtTU4Rgd1C5CryBUhY1WMc+HPk38wmWo6HzNOlhT1HltiPjb4kpSUP+vz5LTtplOqwomw/XBp/wuXuS2ijzCVo6lovtUzra5lsGa9EijHPreXt2dEHy68bTZBt2Os4BeWCMTz58d4wvSvC/hHNXdd/asx1CcW288HFxWRxoNLLawanDILCZLdRln4MwlGP1IruOuK0wJOkP3kxqHJdCL51psBPWDpPTzW0VC9c="
        ));

        final HeroAttributes attributes = getAttributes();
        attributes.setHealth(80.0d);
        attributes.setAttackSpeed(150);

        final Equipment equipment = getEquipment();

        equipment.setChestPlate(99, 8, 16, TrimPattern.SILENCE, TrimMaterial.NETHERITE);
        equipment.setLeggings(28, 3, 7);
        equipment.setBoots(5, 3, 23, TrimPattern.HOST, TrimMaterial.NETHERITE);

        setWeapon(
                new Weapon(Material.GHAST_TEAR)
                        .setName("Vampire's Fang")
                        .setDescription("""
                                A sharp fang.
                                """)
                        .setDamage(5.0d)
        );

        final UltimateTalent ultimate = new BloodfiendUltimate();

        ultimate.setDescription("""
                After a short casting time, impel all &cbitten &cenemies&7 for {duration}.
                &8&o;;While casting, transform into a bat and fly freely.
                                
                While impelled, &nplayers &nmust&7 obey &b&l%s &7of your commands.
                &8&oEntities other than players take %s damage.
                                
                If &4failed&7 to obey a command, they suffer &c%.0f&7 ❤ damage.
                """.formatted(impelTimes, impelNonPlayerDamage, impelDamage));

        setUltimate(ultimate);
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        playerData.values().forEach(BloodfiendData::reset);
        playerData.clear();
    }

    @Override
    public void onStart(@Nonnull GameInstance instance) {
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
    public void onPlayersRevealed(@Nonnull GamePlayer player) {
        getData(player).cooldownFlight(true);
    }

    @Override
    public void onPlayerRespawned(@Nonnull GamePlayer player) {
        onPlayersRevealed(player);
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        super.onStart(player);
    }

    @EventHandler()
    public void handleTalentUse(TalentUseEvent ev) {
        final GamePlayer player = ev.getPlayer();

        workImpel(player, (impel, gp) -> {
            impel.complete(player, Type.USE_ABILITY);
        });
    }

    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getDamagerAsPlayer();
        final LivingGameEntity entity = instance.getEntity();
        final EnumDamageCause cause = instance.getCause();

        if (player == null || cause != EnumDamageCause.ENTITY_ATTACK) {
            return;
        }

        entity.addEffect(Effects.IMMOVABLE, 1);

        final BloodfiendData data = getData(player);

        data.addSucculence(entity);

        // Blood Chalice
        final BloodChaliceTalent chalice = getThirdTalent();
        final Taunt taunt = chalice.getTaunt(player);

        if (taunt == null) {
            return;
        }

        if (taunt.isSuckedEntityAndWithinRange(entity)) {
            final double damage = instance.getDamage();
            final double healing = damage * chalice.healingPercent;

            player.heal(healing);
        }
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
    public TwinClaws getFirstTalent() {
        return (TwinClaws) TalentRegistry.TWIN_CLAWS;
    }

    @Override
    public CandlebaneTalent getSecondTalent() {
        return (CandlebaneTalent) TalentRegistry.CANDLEBANE;
    }

    @Override
    public BloodChaliceTalent getThirdTalent() {
        return (BloodChaliceTalent) TalentRegistry.BLOOD_CHALICE;
    }

    @Override
    public Talent getPassiveTalent() {
        return TalentRegistry.SUCCULENCE;
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
        if (player.isUsingUltimate()) {
            ev.setCancelled(true);
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
        final int succulencePlayers = data.getSuckedCount();
        final int flightCooldown = data.getFlightCooldown();

        final CandlebaneTalent twinClaws = getSecondTalent();
        final BloodChaliceTalent bloodChalice = getThirdTalent();

        final Taunt pillar = twinClaws.getTaunt(player);
        final Taunt chalice = bloodChalice.getTaunt(player);

        return List.of(
                succulencePlayers > 0 ? "&c&l🦇 &f" + succulencePlayers : "",
                flightCooldown > 0 ? "&2&l\uD83D\uDD4A &f" + CFUtils.formatTick(flightCooldown) : "",
                pillar != null ? "&6&lⅡ &f" + CFUtils.formatTick(pillar.getTimeLeft()) : "",
                chalice != null ? "&4&l🍷 &f" + CFUtils.formatTick(chalice.getTimeLeft()) : ""
        );
    }

    @Override
    public boolean processInvisibilityDamage(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, double damage) {
        if (player.isUsingUltimate()) {
            return false;
        }

        return super.processInvisibilityDamage(player, entity, damage);
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

    private class BloodfiendUltimate extends UltimateTalent {

        public BloodfiendUltimate() {
            super(Bloodfiend.this, "Impel", 50);

            setType(TalentType.IMPAIR);
            setItem(Material.MOOSHROOM_SPAWN_EGG);
            setDuration(impelDuration * impelTimes);
            setCastDuration(30);
        }

        @Nonnull
        @Override
        public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
            final BloodfiendData data = getData(player);
            final Set<LivingGameEntity> suckedEntities = data.getSuckedEntities();
            final Location location = player.getLocation().add(0.0d, 0.5d, 0.0d);

            final HumanNPC npc = new HumanNPC(player.getLocation(), "", player.getName());

            npc.showAll();
            npc.setEquipment(player.getEquipment());
            npc.setSitting(true);

            // Draw particles
            suckedEntities.forEach(entity -> {
                new EntityFollowingParticle(2, location, entity) {
                    @Override
                    public void draw(int tick, @Nonnull Location location) {
                        player.spawnWorldParticle(location, Particle.LAVA, 1, 0.1d, 0.1d, 0.1d, 0.0f);
                        player.spawnWorldParticle(location, Particle.FLAME, 1);
                        player.spawnWorldParticle(location, Particle.SMALL_FLAME, 3, 0.1d, 0.1d, 0.1d, 0.05f);
                    }

                    @Override
                    public void onHit(@Nonnull Location location) {
                        if (entity instanceof GamePlayer) {
                            return;
                        }

                        entity.damage(impelNonPlayerDamage, player, EnumDamageCause.IMPEL);
                    }
                }.runTaskTimer(0, 1);
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

            player.addEffect(Effects.INVISIBILITY, 10000, true);

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
                    player.spawnWorldParticle(player.getLocation(), Particle.SMOKE, 2, 0.15d, 0.15d, 0.15d, 0.0f);
                }
            }.runTaskTimer(0, 1);

            player.playWorldSound(location, Sound.ENTITY_BAT_TAKEOFF, 0.25f);

            playSoundAtTick(
                    location,
                    Sound.ENTITY_ELDER_GUARDIAN_CURSE,
                    0.75f,
                    0.1f,
                    0, 2, 3, 5, 6, 9, 10, 12
            );

            return new UltimateResponse() {
                @Override
                public void onCastFinished(@Nonnull GamePlayer player) {
                    getData(player).newImpelInstance(Bloodfiend.this).nextImpel(0);

                    fxBats.forEach(Bat::remove);
                    fxBats.clear();

                    player.setFlySpeed(flySpeed);
                    player.setAllowFlight(false);
                    player.setFlying(false);

                    data.cooldownFlight(true);

                    player.removeEffect(Effects.INVISIBILITY);

                    npc.remove();
                    playerBat.remove();
                    batTask.cancel();
                }
            };
        }
    }

}
