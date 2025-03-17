package me.hapyl.fight.game.heroes.ronin;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
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
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;

public class Ronin extends Hero implements Listener, PlayerDataHandler<RoninData> {

    private final PlayerDataMap<RoninData> playerDataMap = PlayerMap.newDataMap(RoninData::new);

    public Ronin(@Nonnull Key key) {
        super(key, "Ronin");

        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DAMAGE, Archetype.MELEE, Archetype.TALENT_DAMAGE);
        profile.setGender(Gender.MALE);

        setDescription("""
                A samurai.
                """);

        setItem("267bf069fefb40be22724b02e6c4fbe2133ef5e112bc551a4f0042ea99dcf6a2");

        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(5, 2, 41, TrimPattern.SNOUT, TrimMaterial.GOLD);
        equipment.setLeggings(Material.NETHERITE_LEGGINGS, TrimPattern.SHAPER, TrimMaterial.NETHERITE);
        equipment.setBoots(5, 2, 41, TrimPattern.TIDE, TrimMaterial.GOLD);

        setWeapon(new RoninWeapon());
        setUltimate(new RoninUltimate());
    }

    @Nonnull
    @Override
    public RoninWeapon getWeapon() {
        return (RoninWeapon) super.getWeapon();
    }

    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final LivingGameEntity damager = instance.getDamager();
        final LivingGameEntity entity = instance.getEntity();

        if (!(damager instanceof GamePlayer player) || !validatePlayer(player) || !instance.isDirectDamage()) {
            return;
        }

        // Apply bleed
        if (player.isUsingUltimate()) {
            entity.addEffect(EffectType.BLEED, 60);
        }
    }

    @Override
    public Talent getFirstTalent() {
        return TalentRegistry.CHARGE_ATTACK;
    }

    @Override
    public Talent getSecondTalent() {
        return TalentRegistry.RONIN_DASH;
    }

    @Override
    public Talent getPassiveTalent() {
        return null;
    }

    @Nonnull
    @Override
    public PlayerDataMap<RoninData> getDataMap() {
        return playerDataMap;
    }

    private class RoninUltimate extends UltimateTalent {

        @DisplayField(percentage = true) private final double healthSacrifice = 0.5d;

        @DisplayField private final double speedIncrease = 20;
        @DisplayField private final double critChanceIncrease = 20;
        @DisplayField private final double critDamageIncrease = 40;

        private final TemperInstance temperInstance = Temper.RONIN.newInstance("&4&lﾒ &cHakariki")
                                                                  .increaseScaled(AttributeType.SPEED, speedIncrease)
                                                                  .increaseScaled(AttributeType.CRIT_CHANCE, critChanceIncrease)
                                                                  .increaseScaled(AttributeType.CRIT_DAMAGE, critDamageIncrease);

        private final BlockData particleData = Material.NETHER_WART_BLOCK.createBlockData();

        public RoninUltimate() {
            super(Ronin.this, "Harakiri", 60);

            setDescription("""
                    Perform an ancient ritual, losing {healthSacrifice} of your current health.
                    
                    In return, gain the following for {duration}:
                    • Increased %s.
                    • Increased %s.
                    • Increased %s.
                    • Your attacks apply &cbleed&7.
                    """.formatted(AttributeType.SPEED, AttributeType.CRIT_CHANCE, AttributeType.CRIT_DAMAGE));

            setType(TalentType.ENHANCE);
            setItem(Material.REDSTONE);

            setDurationSec(15);
            setCastDurationSec(0.75f);
        }

        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player) {
            return builder()
                    .onCastStart(() -> {
                        // Fx
                        player.addEffect(EffectType.SLOW, 3, getCastDuration());
                        player.spawnWorldParticle(player.getLocation(), Particle.DUST_PILLAR, 20, 0.1, 0.1, 0.1, particleData);
                        player.playHurtAnimation(0);

                        player.playWorldSound(Sound.ENTITY_BREEZE_DEFLECT, 0.5f);
                        player.playWorldSound(Sound.ENTITY_BLAZE_HURT, 0.5f);
                        player.playWorldSound(Sound.ENTITY_PLAYER_HURT, 0.75f);
                    })
                    .onCastEnd(() -> {
                        player.setHealth(player.getHealth() * 0.5d);
                        temperInstance.temper(player, getDuration(), player);
                    });
        }
    }
}
