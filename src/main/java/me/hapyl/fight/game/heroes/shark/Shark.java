package me.hapyl.fight.game.heroes.shark;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.shark.SharkPassive;
import me.hapyl.fight.game.talents.shark.SubmergeTalent;
import me.hapyl.fight.game.talents.shark.Whirlpool;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.terminology.EnumTerm;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Set;

public class Shark extends Hero implements Listener, PlayerDataHandler<SharkData>, UIComponent, Disabled {

    private final PlayerDataMap<SharkData> playerData = PlayerMap.newDataMap(player -> new SharkData(this, player));

    private final int heartBeatSoundEffectDuration = 40;
    private final double heartBeatHealthThreshold = 0.25d;

    public Shark(@Nonnull Key key) {
        super(key, "Shark");

        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DAMAGE, Archetype.MELEE, Archetype.SELF_BUFF, Archetype.HEXBANE);
        profile.setGender(Gender.FEMALE);
        profile.setRace(Race.SHARK);

        setDescription("""
                A strong warrior from the Depths of Waters.
                """);
        setItem("3447e7e8271f573969f2da734c4125f93b2864fb51db69da5ecba7487cf882b0");

        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(157, 175, 194, TrimPattern.RIB, TrimMaterial.QUARTZ);
        equipment.setLeggings(157, 175, 194, TrimPattern.RIB, TrimMaterial.QUARTZ);

        equipment.setBoots(ItemBuilder.leatherBoots(Color.fromRGB(157, 175, 194))
                .setArmorTrim(TrimPattern.RIB, TrimMaterial.QUARTZ)
                .addEnchant(Enchantment.DEPTH_STRIDER, 5)
                .cleanToItemSack());

        setWeapon(Weapon.createBuilder(Material.QUARTZ, Key.ofString("claws"))
                .name("Claws")
                .description("Using one's claws is a better idea than using a stick, don't you think so?")
                .damage(7.0d)
        );

        setUltimate(new SharkUltimate());
    }

    @Override
    public void onStart(@Nonnull GameInstance instance) {
        new TickingGameTask() {
            @Override
            public void run(int tick) {
                final Set<GamePlayer> alivePlayers = getAlivePlayers();

                alivePlayers.forEach(player -> {
                    final SharkData data = getPlayerDataOrNull(player);

                    if (data == null) {
                        return;
                    }

                    final short minStacksForHeartbeat = getPassiveTalent().minStacksForHeartbeat;
                    final int stacks = data.getBloodThirstStacks();

                    // Update attribute
                    getPassiveTalent().temper(player, stacks);

                    if (stacks < minStacksForHeartbeat) {
                        return;
                    }

                    // Heartbeat
                    Collect.nearbyEntities(player.getLocation(), 15, entity -> !player.isSelfOrTeammate(entity))
                            .forEach(entity -> {
                                final double health = entity.getHealth();
                                final double maxHealth = entity.getMaxHealth();

                                if (health / maxHealth > heartBeatHealthThreshold) {
                                    return;
                                }

                                final Location entityLocation = entity.getMidpointLocation();

                                if (modulo(minStacksForHeartbeat)) {
                                    player.playSound(entityLocation, Sound.BLOCK_CONDUIT_AMBIENT, 2.0f);
                                }

                                player.spawnParticle(
                                        entityLocation,
                                        Particle.DUST_COLOR_TRANSITION,
                                        20,
                                        0.33d,
                                        0.33d,
                                        0.33d,
                                        new Particle.DustTransition(Color.fromRGB(255, 77, 77), Color.fromRGB(139, 0, 0), 1)
                                );
                            });
                });
            }
        }.runTaskTimer(0, 5);
    }

    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getDamagerAsPlayer();

        if (player == null || !instance.isDirectDamage()) {
            return;
        }

        final SharkData data = getPlayerData(player);
        data.addBloodThirstStack();
    }

    @Nonnull
    @Override
    public PlayerDataMap<SharkData> getDataMap() {
        return playerData;
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        player.addEffect(EffectType.WATER_BREATHING, -1);
    }

    @Override
    public SubmergeTalent getFirstTalent() {
        return TalentRegistry.SUBMERGE;
    }

    @Override
    public Whirlpool getSecondTalent() {
        return TalentRegistry.WHIRLPOOL;
    }

    @Override
    public SharkPassive getPassiveTalent() {
        return TalentRegistry.SHARK_PASSIVE;
    }

    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final SharkData data = getPlayerData(player);
        final int stacks = data.getBloodThirstStacks();

        return "&4%s &c&l%s".formatted(Named.BLOOD_THIRST.getPrefix(), stacks);
    }

    private class SharkUltimate extends UltimateTalent {

        @DisplayField private final short minBloodThirst = 4;
        @DisplayField private final double damage = 50.0d;
        @DisplayField private final double radius = 5.0d;

        private final int maxDashTime = 100;

        public SharkUltimate() {
            super(Shark.this, "Ocean Madness", 60);

            setDescription("""
                    Gather the water within and leap high in the air.
                    
                    After a short casting time, ride the wave forward rapidly, dealing massive %s upon landing.
                    
                    &8;;If you posses at least {minBloodThirst}, your amazing eyesight will mark hurt enemies.
                    """.formatted(EnumTerm.PIERCING_DAMAGE)
            );

            setMaterial(Material.WATER_BUCKET);

            setDurationSec(3);
            setCastDuration(20);
        }


        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged) {
            final SharkData data = getPlayerData(player);
            final int stacks = data.getBloodThirstStacks();
            final boolean strongAttack = stacks >= minBloodThirst;

            player.setAttributeValue(Attribute.GRAVITY, 0.0d);
            player.setVelocity(player.getDirection().normalize().setY(0.75d));

            return new UltimateInstance() {
                private Vector vector;

                @Override
                public void onCastEnd() {
                    if (strongAttack) {
                        Collect.nearbyEntities(player.getLocation(), 100, entity -> entity.getHealth() <= damage)
                                .forEach(entity -> {
                                    player.spawnParticle(entity.getEyeLocation(), Particle.FLASH, 1, 0, 0, 0, 0);
                                });
                    }
                }

                @Override
                public void onExecute() {
                    player.setAttributeValue(Attribute.GRAVITY, BukkitUtils.GRAVITY);
                    player.addEffect(EffectType.FALL_DAMAGE_RESISTANCE, 1000);
                }

                @Override
                public void onTick(int tick) {
                    if (player.isOnGround() || tick >= maxDashTime) {
                        doLand();
                        return;
                    }

                    player.setVelocity(getVector());
                }

                private Vector getVector() {
                    if (vector == null) {
                        final Vector direction = player.getDirection().normalize();

                        vector = direction.multiply(2.5d);
                        vector.setY(Math.min(vector.getY(), -0.5d));
                    }

                    return vector;
                }

                private void doLand() {
                    forceEndUltimate();

                    final Location location = player.getLocation();

                    Collect.nearbyEntities(location, radius, entity -> !player.isSelfOrTeammate(entity))
                            .forEach(entity -> {
                                entity.damage(damage, player, DamageCause.SHARK_BITE);
                            });

                    // Fx
                }
            };
        }
    }
}
