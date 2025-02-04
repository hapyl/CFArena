package me.hapyl.fight.game.heroes.vampire;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.player.PlayerSkin;
import me.hapyl.eterna.module.reflect.npc.HumanNPC;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.BloodDebt;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.vampire.BatTransferTalent;
import me.hapyl.fight.game.talents.vampire.BloodDebtTalent;
import me.hapyl.fight.game.talents.vampire.VampirePassive;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Set;

public class Vampire extends Hero implements Listener {

    public Vampire(@Nonnull Key key) {
        super(key, "Vorath");

        setDescription("""
                One of the royal guards at the %s&8&o, believes that with enough firepower, &oeverything&8&o is possible.
                
                Prefers NoSunBurn™ sunscreen.
                """.formatted(Affiliation.CHATEAU));

        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DAMAGE, Archetype.SELF_SUSTAIN, Archetype.SELF_BUFF);
        profile.setAffiliation(Affiliation.CHATEAU);
        profile.setGender(Gender.MALE);
        profile.setRace(Race.VAMPIRE);

        setItem("25a7007007d5a396d6049c71ab6ff5fedb6ca3e1753b3fd6f13bb6946a7e0daf");

        final HeroAttributes attributes = getAttributes();
        attributes.setMaxHealth(90);

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(191, 57, 66, TrimPattern.COAST, TrimMaterial.NETHERITE);
        equipment.setLeggings(191, 57, 66, TrimPattern.SILENCE, TrimMaterial.NETHERITE);

        setWeapon(Weapon.builder(Material.GHAST_TEAR, Key.ofString("vampires_fang"))
                .name("Vampire's Fang")
                .description("""
                        A very sharp fang.
                        """)
                .damage(5.0d)
        );

        setUltimate(new VampireUltimate());
    }

    @EventHandler
    public void handleGameDamageEvent(GameDamageEvent ev) {
        final GameEntity damager = ev.getDamager();

        if (!(damager instanceof GamePlayer player) || !validatePlayer(player)) {
            return;
        }

        final BloodDebt bloodDebt = player.bloodDebt();

        if (!bloodDebt.hasDebt()) {
            return;
        }

        // Increase damage based on blood debt
        final double bloodDebtAmount = bloodDebt.amount();
        final double decrement = Math.min(bloodDebtAmount, player.getMaxHealth() * 0.15d);
        final double damageIncrease = 1 + bloodDebtAmount * TalentRegistry.BLOOD_DEBT.damageIncreaseMultiplier;

        bloodDebt.decrement(decrement);
        ev.multiplyDamage(damageIncrease);
    }

    @Override
    public BloodDebtTalent getFirstTalent() {
        return TalentRegistry.BLOOD_DEBT;
    }

    @Override
    public BatTransferTalent getSecondTalent() {
        return TalentRegistry.BAT_TRANSFER;
    }

    @Override
    public VampirePassive getPassiveTalent() {
        return TalentRegistry.VANPIRE_PASSIVE;
    }

    private class VampireUltimate extends UltimateTalent {

        @DisplayField private final int batsDuration = Tick.fromSecond(20);

        @DisplayField private final double homingSpeed = 0.5d;
        @DisplayField private final double damage = 10;

        @DisplayField(percentage = true) private final double bloodDebtAmount = 0.2;

        private final double biteThreshold = 1.0d;

        public VampireUltimate() {
            super(Vampire.this, "Legion!", 75);

            setDescription("""
                    Blow into the war horn summoning a vampire army.
                    
                    After a short delay, the army transforms into bats and rushes forward, dealing damage and applying %1$s to hit enemies.
                    
                    Also clear your own %1$s, heal based on the amount cleared and refresh %2$s cooldown.
                    """.formatted(Named.BLOOD_DEBT, getFirstTalent().getName()));

            setItem(Material.GOAT_HORN);
            setType(TalentType.DAMAGE);

            setCooldownSec(35);
            setCastDurationSec(2.5f);
        }

        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player) {
            return new VampireUltimateInstance(this, player);
        }

    }

    private class VampireUltimateInstance extends UltimateInstance {

        private static final int ARMY_COUNT = 11;
        private static final PlayerSkin SOLDIER_SKIN = PlayerSkin.of(
                "ewogICJ0aW1lc3RhbXAiIDogMTYyMzY3MjUyOTE3NywKICAicHJvZmlsZUlkIiA6ICJmMTA0NzMxZjljYTU0NmI0OTkzNjM4NTlkZWY5N2NjNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJ6aWFkODciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjkzNzI3Yjc5NzY4ZDQ0MmMxYWYxODAyN2RjZWQwOTU5ZmUzNjU0YTVmMzI4ZGM4YjZlNDVlNzkzMWFkN2NlYiIKICAgIH0KICB9Cn0=",
                "WVe6ioBtctFGz0zdkZexpOCzTh9JvGpzlWrh+E+4UvqEOQlZtKplAuZxMtvJBsSHBbJawJ86JSTk4Em/jANqmISJFx+clQDxVGUQywKRwUqcKZvs8lpPtVfNlr9M+YYXvCJK5r5JOZ++2WEJ4232qTNm+YTCuDMFbpHGYP3/f94UinVg2B2vExsNQRTaqTMNfDffysm1Y5RILqqbESt/XY8P5fwZCOdl0+rvX3STWUTDUx5UYmVHqvhmiGcwXJLW1Nb0YbEWDxqnb4yVPWMxpHjDZ5Es+nV/08pfzQ+HBxw735MIpb1rjH8QbuMsrUJg65ixKYnGeadAVpefQROMpotgeMoDy49hg7/utw1tDT+aBgHt6yWiDTsao2RQ+SyYUxb6Kvrn+zaqSI4NENQJ/12aECpd8jq9g6XbbotgsII6gTAYk/fieB5xenn+8AVHCe2rUfF++BelFljZiXX8SvQkH2dK2mrvz0N/CWdM4oEMsew02/n710BhN0MQG3WyfqCmwlMmNib6kyLAHUcS6hTc7qSz+l+wNQTgSJ6C+wK50Qvqhj8MesSet+UWBJhD7kzDiWKo6ZfouJNNNLWhIFAcWGqbZjsaSKXDNfW7yVOUHjf6cbl7akzDRu862WXlrsooyT+zErlnwnVVorS1rSnNAmKtMWUycXJ0NpcyjXA="
        );

        private final VampireUltimate ultimate;
        private final GamePlayer player;
        private final Set<HumanNPC> army;

        private VampireUltimateInstance(VampireUltimate ultimate, GamePlayer player) {
            this.ultimate = ultimate;
            this.player = player;
            this.army = Sets.newHashSet();
        }

        @Override
        public void onCastStart() {
            // Summon army
            summonArmy();

            // Fx
            player.playWorldSound(Sound.ITEM_GOAT_HORN_SOUND_2, 1.25f);
        }

        @Override
        public void onExecute() {
            final Set<Bat> bats = Sets.newHashSet();

            CollectionUtils.forEachAndClear(
                    army, soldier -> {
                        bats.add(Entities.BAT.spawn(
                                soldier.getLocation(), self -> {
                                    self.setInvulnerable(true);
                                    self.setAwake(true);
                                    self.setAI(false);

                                    // Fx
                                }
                        ));

                        soldier.remove();
                    }
            );

            new TickingGameTask() {
                @Override
                public void onTaskStop() {
                    CollectionUtils.forEachAndClear(bats, Entity::remove);
                }

                private void doDamage(Bat bat, LivingGameEntity entity) {
                    bat.remove();

                    entity.damage(ultimate.damage, player, EnumDamageCause.BAT_BITE_NO_TICK);
                    entity.bloodDebt().incrementOfMaxHealth(ultimate.bloodDebtAmount);

                    // TODO (Tue, Feb 4 2025 @xanyjl): Add fx
                }

                @Override
                public void run(int tick) {
                    bats.removeIf(Bat::isDead);

                    if (tick >= ultimate.batsDuration || bats.isEmpty()) {
                        cancel();
                        return;
                    }

                    // Push bats
                    bats.forEach(bat -> {
                        final Location location = bat.getLocation();

                        // Bats home towards closest enemies because it would be impossible to hit otherwise
                        final LivingGameEntity nearestEntity = Collect.nearestEntity(location, 5, player::isNotSelfOrTeammate);
                        final Vector direction;

                        if (nearestEntity != null) {
                            direction = nearestEntity.getMidpointLocation().toVector().subtract(location.toVector()).normalize();

                            final double distance = nearestEntity.getEyeLocation().distanceSquared(location);

                            if (distance <= ultimate.biteThreshold) {
                                doDamage(bat, nearestEntity);
                                return;
                            }
                        }
                        else {
                            direction = location.getDirection();
                        }

                        // TODO (Tue, Feb 4 2025 @xanyjl): Block collision

                        // Transfer
                        location.add(direction.multiply(ultimate.homingSpeed));
                        bat.teleport(location);
                    });
                }
            }.runTaskTimer(0, 1);

            // Fx
            player.playWorldSound(Sound.ENTITY_ENDER_DRAGON_GROWL, 2.0f);
        }

        private void summonArmy() {
            for (int i = 0; i < ARMY_COUNT; i++) {
                final Location location = pickRandomLocationBehindPlayer();

                final HumanNPC soldier = new HumanNPC(location, "");

                soldier.setSkin(SOLDIER_SKIN.getTexture(), SOLDIER_SKIN.getSignature());
                soldier.showAll();

                location.add(0, 1, 0);

                // Fx
                player.spawnWorldParticle(location, Particle.LARGE_SMOKE, 5, 0.1, 0.2, 0.1, 0.025f);
                player.spawnWorldParticle(location, Particle.SMOKE, 5, 0.1, 0.2, 0.1, 0.025f);

                army.add(soldier);
            }
        }

        private Location pickRandomLocationBehindPlayer() {
            final double zOffset = player.random.nextDouble(3, 6);
            final double xOffsetPositive = player.random.nextDoubleBool(3);
            final double xOffsetNegative = player.random.nextDoubleBool(3);

            Location location = player.getLocationBehindFromEyes(zOffset);
            location.setPitch(0.0f);

            location = LocationHelper.getToTheLeft(location, xOffsetPositive);
            location = LocationHelper.getToTheRight(location, xOffsetNegative);

            return BukkitUtils.anchorLocation(location);
        }
    }
}
