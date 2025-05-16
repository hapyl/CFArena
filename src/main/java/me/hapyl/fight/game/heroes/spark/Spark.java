package me.hapyl.fight.game.heroes.spark;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.spark.FireGuyPassive;
import me.hapyl.fight.game.talents.spark.Molotov;
import me.hapyl.fight.game.talents.spark.SparkFlash;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.*;

import javax.annotation.Nonnull;

public class Spark extends Hero implements PlayerDataHandler<SparkData> {

    @DisplayField(percentage = true) private final double inWaterDamage = 0.03d;

    private final PlayerDataMap<SparkData> playerData = PlayerMap.newDataMap(SparkData::new);

    public Spark(@Nonnull Key key) {
        super(key, "Spark");

        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.RANGE, Archetype.POWERFUL_ULTIMATE, Archetype.SELF_SUSTAIN);
        profile.setGender(Gender.MALE);

        setDescription("""
                Strikes with fire! ...literally.
                """);
        setItem("ade095332720215ca9b85e7eacd1d092b1697fad34d696add94d3b70976702c");

        final HeroEquipment equipment = this.getEquipment();
        equipment.setChestPlate(Color.ORANGE);
        equipment.setLeggings(Color.RED);
        equipment.setBoots(Color.ORANGE);

        setWeapon(new SparkWeapon());
        setUltimate(new SparkUltimate());
    }

    @Nonnull
    @Override
    public PlayerDataMap<SparkData> getDataMap() {
        return playerData;
    }

    @Override
    public void processDamageAsVictim(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getEntityAsPlayer();
        final DamageCause cause = instance.getCause();

        if (!validatePlayer(player)) {
            return;
        }

        // Check for ultimate death
        if (player.isUsingUltimate() && instance.getDamage() >= player.getHealth()) {
            getPlayerData(player).rebirth();
            player.setUsingUltimate(false);

            instance.setCancelled(true);
            return;
        }

        // Cancel any fire damage
        if (cause.isFireDamage()) {
            instance.setCancelled(true);
        }
    }

    @Override
    public void onStart(@Nonnull GameInstance instance) {
        new TickingGameTask() {
            @Override
            public void run(int tick) {
                getAlivePlayers().forEach(player -> {
                    if (player.isInWater()) {
                        player.damage(inWaterDamage, DamageCause.WATER);
                        player.playWorldSound(Sound.ENTITY_PLAYER_HURT_DROWN, 0.75f);
                    }
                });
            }
        }.runTaskTimer(10, 10);
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        player.addEffect(EffectType.FIRE_RESISTANCE, 1, 999999);
    }

    @Override
    public Molotov getFirstTalent() {
        return TalentRegistry.SPARK_MOLOTOV;
    }

    @Override
    public SparkFlash getSecondTalent() {
        return TalentRegistry.SPARK_FLASH;
    }

    @Override
    public FireGuyPassive getPassiveTalent() {
        return TalentRegistry.FIRE_GUY;
    }

    private class SparkUltimate extends UltimateTalent {
        public SparkUltimate() {
            super(Spark.this, "Run it Back", 80);

            setDescription("""
                    Instantly place a marker at your current location for {duration}.
                    
                    Upon death or after duration ends, safely teleport to the marked location with health you had upon activating the talent.
                    """
            );

            setType(TalentType.ENHANCE);
            setMaterial(Material.TOTEM_OF_UNDYING);
            setDurationSec(6);
            setCooldownSec(40);
        }

        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged) {
            final SparkData data = getPlayerData(player);

            if (data.runItBack != null) {
                return error("Already running back!");
            }

            final Location location = data.markerLocation();
            final double health = player.getHealth();

            data.runItBack = new RunItBackData(location, health);

            return builder()
                    .onExecute(() -> {
                        player.setVisualFire(true);
                    })
                    .onTick(tick -> {
                        final int percent = tick * 10 / getDuration();
                        player.sendSubtitle("&6🔥".repeat(percent) + "&8🔥".repeat(10 - percent), 0, 10, 0);

                        // Fx
                        player.spawnWorldParticle(player.getLocation().add(0.0d, 0.5d, 0.0d), Particle.FLAME, 1, 1.0d, 0.5d, 1.0d, 0.05f);

                        // Fx at marker
                        player.spawnWorldParticle(location, Particle.LANDING_LAVA, 1, 0.2d, 0.2, 0.2d, 0.05f);
                        player.spawnWorldParticle(location, Particle.DRIPPING_LAVA, 1, 0.2d, 0.2, 0.2d, 0.05f);
                    })
                    .onEnd(data::rebirth);
        }
    }
}
